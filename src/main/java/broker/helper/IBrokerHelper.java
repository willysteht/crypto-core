package broker.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import broker.IBroker;
import model.bar.BittrexBar;
import model.market.Market;
import model.market.Market24HSummary;
import model.order.OrderBook;
import model.order.TradingOrder;
import model.tick.Ticker;
import utils.Interval;
import utils.Satoshi;

public interface IBrokerHelper {
	
	/**
	 * Returns the amount of free bitcoin
	 * @return amount of free bitcoin
	 */
	double getFreeBalance();
	
	/**
	 * Returns the total amount of bitcoin
	 * @return total amount of bitcoin
	 */
	double getTotalBalance();
	
	/**
	 * Returns the amount of bitcoin used in trading
	 * @return amount of bitcoin used in trading
	 */
	double getTransactionBalance();
	
	/**
	 * return the amount of free coins of specified cryptocoin
	 * @param coin: cryptocoin
	 * @return amount of free coins of specified cryptocoin
	 */
	double getFreeBalance(String coin);
	
	/**
	 * return the total amount of coins of specified cryptocoin
	 * @param coin: cryptocoin
	 * @return total amount of coins of specified cryptocoin
	 */
	double getTotalBalance(String coin);
	
	/**
	 * return the amount of coins of specified cryptocoin used in trading
	 * @param coin: cryptocoin
	 * @return amount of coins of specified cryptocoin used in trading
	 */
	double getTransactionBalance(String coin);

	/**
	 * Gets the Tradingbalance of the chosen Broker
	 */
	void loadTradingBalance();
	/**
	 * returns whether the Tradingbalance is loaded or not
	 * @return
	 */
	boolean isTradingBalanceLoaded();
	/**
	 * Reloads the Tradingbalance of the chosen Broker
	 */
	void reloadTradingBalance();
	/**
	 * Sets the broker to use
	 * @param broker: Broker which will be used
	 */
	void setBroker(IBroker broker) throws IllegalArgumentException;

	JSONObject buy(String name, Satoshi buyAmount, Satoshi buyPrice);

	JSONObject sell(String name, Satoshi balance, Satoshi sellPrice);

	JSONObject cancelOrder(String orderId);

	OrderBook loadOrderBook(String name);

	void reloadMarketSummaries();

	Set<String> getMarketNames() throws Exception;
	Set<String> getMarketNames(String filter) throws Exception;

	List<BittrexBar> getBars(String marketName, Interval interval) throws Exception;

	Map<String, Market24HSummary> getMarketSummaries() throws Exception;

	Map<String, Satoshi> getMyBalances() throws Exception;

	void reloadMyBalances();
	
	/**
	 * Reloads Bars for a specific market from the API.
	 * @param marketName
	 * @param interval
	 */
	void reloadBars(String marketName, Interval interval);

	void reloadMyMarkets() throws Exception;
	
	Set<Market> getMarketsIOwn() throws Exception;
	
	Set<Market> getMarketsIDoNotOwn() throws Exception;

	List<TradingOrder> loadOrderHistory();

	Ticker loadTicker(String marketName);
}
