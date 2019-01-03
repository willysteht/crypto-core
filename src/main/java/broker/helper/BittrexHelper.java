package broker.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import broker.BittrexBroker;
import broker.IBroker;
import javafx.util.Pair;
import model.bar.BittrexBar;
import model.market.Market;
import model.market.Market24HSummary;
import model.order.OrderBook;
import model.order.OrderBookOrder;
import model.order.OrderBookOrder.Type;
import model.order.TradingOrder;
import model.tick.Ticker;
import utils.Interval;
import utils.Satoshi;

public class BittrexHelper implements IBrokerHelper {
	private BittrexBroker broker;
	private Set<String> marketNames;
	private Set<Market> marketsIOwn;
	private Set<Market> marketsIDoNotOwn;
	private Map<String, Market24HSummary> marketSummaries;
	private Map<String, Satoshi> myBalances;
	private Map<String, Map<Interval, List<BittrexBar>>> bars;

	public BittrexHelper() {
		broker = null;
		marketNames = new HashSet<>();
		marketsIDoNotOwn = new HashSet<>();
		marketsIOwn = new HashSet<>();
		marketSummaries = new HashMap<>();
		myBalances = new HashMap<>();
		bars = new ConcurrentHashMap<>();

	}

	@Override
	public void setBroker(IBroker broker) throws IllegalArgumentException {
		if (!(broker instanceof BittrexBroker))
			throw new IllegalArgumentException("Please use a Bittrex Broker!");
		this.broker = (BittrexBroker) broker;
	}

	public void reloadMarketSummaries() {
		marketSummaries.clear();

		JSONObject marketSum = broker.getMarketSummaries();
		if (marketSum != null) {
			JSONArray result = marketSum.getJSONArray("result");

			for (Object m : result) {
				JSONObject market = (JSONObject) m;
				Market24HSummary ms = new Market24HSummary(market.getJSONObject("Summary"),
						market.getBoolean("IsVerified"), market.getJSONObject("Market"));
				marketSummaries.put(ms.getMarketName(), ms);
			}

		}
	}

	@Override
	public void reloadBars(String marketName, Interval interval) {
		JSONArray jBars = null;
		try {
			jBars = broker.getTicker(marketName, interval).getJSONArray("result");
			if (jBars != null && !(jBars.length() == 0)) {
				for (Object jo : jBars) {
					JSONObject bar = (JSONObject) jo;
					Date d = (Date) new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(bar.getString("T"));

					List<BittrexBar> l = null;
					Map<Interval, List<BittrexBar>> m = null;

					if (!bars.isEmpty() && bars.get(marketName) != null) {
						m = bars.get(marketName);
						l = bars.get(marketName).get(interval);
					} else {
						l = new LinkedList<>();
						m = new HashMap<>();
					}

					l.add(new BittrexBar(bar.getDouble("C"), d, bar.getDouble("BV"), bar.getDouble("H"),
							bar.getDouble("L"), bar.getDouble("O")));

					m.put(interval, l);

					bars.put(marketName, m);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("jbars = " + jBars);
			System.out.println("getticker " + broker.getTicker(marketName, interval));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public List<TradingOrder> loadOrderHistory() {
		return extractCorrectOrder(broker.getOrderHistory().getJSONArray("result"));
	}

	public List<TradingOrder> loadOpenOrders() {
		return extractCorrectOrder(broker.getOpenOrders().getJSONArray("result"));
	}

	private List<TradingOrder> extractCorrectOrder(JSONArray orders) {
		List<TradingOrder> out = new LinkedList<>();
		for (Object o : orders) {
			JSONObject jo = (JSONObject) o;
			String name = jo.getString("Exchange");
			String orderType = jo.getString("OrderType");
			Satoshi quantity = Satoshi.valueOf(jo.getDouble("Quantity"));
			String orderId = jo.getString("OrderUuid");
			Satoshi commision = Satoshi.valueOf(jo.getDouble("Commission"));
			Satoshi price = Satoshi.valueOf(jo.getDouble("Price"));

			Satoshi rate = null;
			Satoshi cost = null;

			if (jo.has("Id")) { // open order
				System.err.println("yet to implement BittrexHelper::extractCorrectOrder");
				System.exit(0);
				/**
				 * rate = Decimal8.valueOf(jo.getDouble("Limit")); cost =
				 * rate.multipliedBy(quantity).multipliedBy(BittrexBroker.fee - 0.005);
				 */

			} else { // order history
				rate = Satoshi.valueOf(jo.getDouble("PricePerUnit"));
				if (orderType.equals(TradingOrder.ORDER_TYPE_LIMIT_BUY)) {
					cost = price.plus(commision);
				} else if(orderType.equals(TradingOrder.ORDER_TYPE_LIMIT_SELL)) {
					cost = price.minus(commision);
				} else {
					System.err.println("ordertype: " + orderType + " not supported BittrexHelper::extractCorrectOrder");
					System.exit(0);
				}
			}

			TradingOrder ord = new TradingOrder(name, orderType, quantity, rate, orderId, cost);
			out.add(ord);
		}
		return out;
	}

	@Override
	public void reloadMyBalances() {
		myBalances.clear();

		JSONArray balances = broker.getBalances();
		for (Object o : balances) {
			JSONObject jo = (JSONObject) o;
			JSONObject bitcoinCurr;
			try {
				bitcoinCurr = jo.getJSONObject("BitcoinMarket");
			} catch (JSONException e) {
				bitcoinCurr = null;
			}

			if (bitcoinCurr != null) { // ist null wenn btc selbst geguckt wird (btc-btc gibts net)
				myBalances.put(bitcoinCurr.getString("MarketName"),
						Satoshi.valueOf(jo.getJSONObject("Balance").getDouble("Balance")));
			} else {
				JSONObject bitcoinBalance = jo.getJSONObject("Balance");
				myBalances.put(bitcoinBalance.getString("Currency"),
						Satoshi.valueOf(bitcoinBalance.getDouble("Available")));
			}
		}
	}

	@Override
	public OrderBook loadOrderBook(String marketName) {
		OrderBook out = new OrderBook();
		try {

			JSONObject jo = broker.getMarketOrderBook(marketName).getJSONObject("result");

			JSONArray buys = jo.getJSONArray("buy");
			JSONArray sells = jo.getJSONArray("sell");

			OrderBookOrder order = null;
			for (Object buy : buys) {
				JSONObject b = (JSONObject) buy;

				Satoshi rate = Satoshi.valueOf(b.getDouble("Rate"));
				Satoshi quantity = Satoshi.valueOf(b.getDouble("Quantity"));

				order = new OrderBookOrder(rate, quantity, Type.Buy);
				out.getBuyOrders().add(order);
			}

			for (Object sell : sells) {
				JSONObject s = (JSONObject) sell;

				Satoshi rate = Satoshi.valueOf(s.getDouble("Rate"));
				Satoshi quantity = Satoshi.valueOf(s.getDouble("Quantity"));

				order = new OrderBookOrder(rate, quantity, Type.Sell);
				out.getSellOrders().add(order);
			}

		} catch (JSONException e) {
			return null;
		}

		return out;
	}

	private LinkedHashSet<Pair<Satoshi, Satoshi>> extractPairs(JSONArray buy) {
		LinkedHashSet<Pair<Satoshi, Satoshi>> out = new LinkedHashSet<>(buy.length());
		for (Object o : buy) {
			JSONObject res = (JSONObject) o;
			out.add(new Pair<Satoshi, Satoshi>((Satoshi) Satoshi.valueOf(res.getDouble("Rate")),
					(Satoshi) Satoshi.valueOf(res.getDouble(("Quantity")))));
		}
		return out;
	}

	@Override
	public JSONObject buy(String name, Satoshi buyAmount, Satoshi buyPrice) {
		return broker.buy(name, buyAmount, buyPrice);
	}

	@Override
	public JSONObject sell(String name, Satoshi balance, Satoshi sellPrice) {
		return broker.sell(name, balance, sellPrice);
	}

	@Override
	public JSONObject cancelOrder(String orderId) {
		return broker.cancelOrder(orderId);
	}

	@Override
	public double getFreeBalance() {
		return getFreeBalance("BTC");
	}

	@Override
	public double getTotalBalance() {
		return getTotalBalance("BTC");
	}

	@Override
	public double getTransactionBalance() {
		return getTransactionBalance("BTC");
	}

	@Override
	public double getFreeBalance(String market) {
		// if(tradingbalancesAvailable.get(market)==null)
		return 0;
		// return tradingbalancesAvailable.get(market);
	}

	@Override
	public double getTotalBalance(String market) {
		// if(tradingbalancesTotal.get(market)==null)
		return 0;
		// System.out.println(tradingbalancesTotal.get(market));
		// return tradingbalancesTotal.get(market);
	}

	@Override
	public double getTransactionBalance(String market) {
		return getTotalBalance(market) - getFreeBalance(market);
	}

	@Override
	public void loadTradingBalance() {
		// marketSummaries = loadMarketSummaries();
		//
		// JSONArray ja = broker.getBalances();
		// for(Object o : ja) {
		// JSONObject json = (JSONObject) o;
		// String currency = json.getJSONObject("Currency").getString("Currency");
		// double available = json.getJSONObject("Balance").getDouble("Available");
		// double balance = json.getJSONObject("Balance").getDouble("Balance");
		//
		// tradingbalancesAvailable.put(currency, available);
		// tradingbalancesTotal.put(currency, balance);
		// }
		// double btcBalance = tradingbalancesTotal.get("BTC");
		//
		// for (String s : tradingbalancesTotal.keySet()) {
		// if (!s.equals("BTC")) {
		// double last = marketSummaries.get("BTC-"+s).getLast().doubleValue();
		// double balance = tradingbalancesTotal.get(s);
		//
		// btcBalance += last*balance;
		// }
		// }
		// tradingbalancesTotal.replace("BTC", btcBalance);
		//
		// isTradingBalanceLoaded = true;
	}

	@Override
	public boolean isTradingBalanceLoaded() {
		// return isTradingBalanceLoaded;
		return false;
	}

	@Override
	public void reloadTradingBalance() {
		// isTradingBalanceLoaded = false;
		// tradingbalancesAvailable.clear();
		// tradingbalancesTotal.clear();
	}

	public Satoshi loadBTCPrice() {
		return Satoshi.valueOf(broker.getBTCPrice().getJSONObject("result").getJSONObject("bpi").getJSONObject("USD")
				.getDouble("rate_float"));
	}

	@Override
	public Set<String> getMarketNames() throws Exception {
		this.marketNames = getMarketSummaries().keySet();
		return marketNames;
	}

	@Override
	public Set<String> getMarketNames(String startsWith) throws Exception {
		Set<String> out = new HashSet<>();
		for (String n : getMarketNames()) {
			if (n.startsWith(startsWith)) {
				out.add(n);
			}
		}
		return out;
	}

	@Override
	public Map<String, Satoshi> getMyBalances() throws Exception {
		if (myBalances != null && !myBalances.isEmpty()) {
			return myBalances;
		} else {
			throw new Exception("Reload myBalances first.");
		}
	}

	@Override
	public Map<String, Market24HSummary> getMarketSummaries() throws Exception {
		if (marketSummaries != null && !marketSummaries.isEmpty()) {
			return marketSummaries;
		} else {
			throw new Exception("Reload marketSummaries first.");
		}

	}

	@Override
	public List<BittrexBar> getBars(String marketName, Interval interval) throws Exception {
		if (bars != null && !bars.isEmpty()) {
			return bars.get(marketName).get(interval);
		} else {
			throw new Exception("Reload bars first.");
		}
	}

	@Override
	public Set<Market> getMarketsIOwn() throws Exception {
		if (marketsIOwn != null && !marketsIOwn.isEmpty()) {
			return marketsIOwn;
		} else {
			throw new Exception("reloadMyMarkets first.");
		}
	}

	@Override
	public Set<Market> getMarketsIDoNotOwn() throws Exception {
		if (marketsIDoNotOwn != null && !marketsIDoNotOwn.isEmpty()) {
			return marketsIDoNotOwn;
		} else {
			throw new Exception("reloadMyMarkets first.");
		}
	}

	@Override
	public void reloadMyMarkets() throws Exception {
		for (String s : getMarketNames()) {
			if (s.startsWith("BTC")) {
				Satoshi balance = getMyBalances().get(s) == null ? Satoshi.ZERO : getMyBalances().get(s);

				Market market = new Market.Builder().setBalance(balance)
						.setMarket24hSummary(getMarketSummaries().get(s)).setMarketName(s)
						.setBitcoinBalance(balance.multipliedBy(getMarketSummaries().get(s).getLast())).build();

				if (market.isInWallet()) {
					marketsIOwn.add(market);
				} else {
					marketsIDoNotOwn.add(market);
				}
			}
		}
	}
	
	@Override
	public Ticker loadTicker(String marketName) {
		JSONObject jo = broker.getTicker(marketName).getJSONObject("result");
		
		Satoshi bid = Satoshi.valueOf(jo.getDouble("Bid"));
		Satoshi ask = Satoshi.valueOf(jo.getDouble("Ask"));
		Satoshi last = Satoshi.valueOf(jo.getDouble("Last"));
		
		return new Ticker(bid, ask, last);
	}
}
