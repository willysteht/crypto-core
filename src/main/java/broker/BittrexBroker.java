package broker;

import java.io.File;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import enums.Interval;
import model.brokerHelper.EncryptionUtility;
import utils.ApikeyHelper;
import utils.Satoshi;
import utils.RestHelper;
import utils.sound.SoundUtils;

public class BittrexBroker implements IBroker {

	public static final String ORDER_LIMIT = "LIMIT", ORDER_MARKET = "MARKET";
	public static final String TRADE_BUY = "BUY", TRADE_SELL = "SELL";
	public static final String TIMEINEFFECT_GOOD_TIL_CANCELLED = "GOOD_TIL_CANCELLED",
			TIMEINEFFECT_IMMEDIATE_OR_CANCEL = "IMMEDIATE_OR_CANCEL", TIMEINEFFECT_FILL_OR_KILL = "FILL_OR_KILL";
	public static final String CONDITION_NONE = "NONE", CONDITION_GREATER_THAN = "GREATER_THAN",
			CONDITION_LESS_THAN = "LESS_THAN", CONDITION_STOP_LOSS_FIXED = "STOP_LOSS_FIXED",
			CONDITION_STOP_LOSS_PERCENTAGE = "STOP_LOSS_PERCENTAGE";
	public static double fee = 1.0025; // 0,25%
	private static final Exception InvalidStringListException = new Exception("Must be in key-value pairs");
	private final String INITIAL_URL = "https://international.bittrex.com/api/";
	private final String METHOD_PUBLIC = "pub", METHOD_KEY = "key";
	private final String MARKET = "market", MARKETS = "markets", CURRENCY = "currency", CURRENCIES = "currencies",
			BALANCE = "balance", ORDERS = "orders";
	private final String encryptionAlgorithm = "HmacSHA512";
	private String API_VERSION = "2.0";

	private String apikey = "";
	private String secret = "";

	private RestHelper restHelper;

	public BittrexBroker() {
		String path = "C:\\sexyMoneymaker\\broker\\";
		String file = "bittrex.txt";
		File f = new File(path + file);

		String[] sa = ApikeyHelper.getApikeyAndSecret(f);
		apikey = sa[0];
		secret = sa[1];

		restHelper = new RestHelper(INITIAL_URL, apikey, secret);
	}

	public BittrexBroker(String apikey, String secret) {
		this.apikey = apikey;
		this.secret = secret;
		restHelper = new RestHelper(INITIAL_URL, apikey, secret);
	}

	public JSONObject getBTCPrice() {
		return getResponse(METHOD_PUBLIC, CURRENCIES, "GetBTCPrice");
	}

	public JSONObject getMarketSummaries() { // Returns a 24-hour summary of all markets
		return getResponse(METHOD_PUBLIC, MARKETS, "getmarketsummaries");
	}

	@Override
	public JSONObject getTicker(String market, Interval interval) {
		return getResponse(METHOD_PUBLIC, MARKET, "GetTicks",
				returnCorrectMap("marketName", market, "tickInterval", interval.toString()));
	}

	@Override
	public JSONObject getTicker(String market) {
		this.API_VERSION = "1.1";
		JSONObject json = getResponse("public", "", "getticker", returnCorrectMap("market", market));
		this.API_VERSION = "2.0";
		return json;
	}

	@Override
	public JSONObject buy(String market, Satoshi amount, Satoshi rate) {
		return placeNonConditionalOrder(TRADE_BUY, market, ORDER_LIMIT, amount, rate, TIMEINEFFECT_GOOD_TIL_CANCELLED);
	}

	@Override
	public void buy(String coinTo, String coinFrom, Satoshi amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject sell(String market, Satoshi amount, Satoshi rate) {
		return placeNonConditionalOrder(TRADE_SELL, market, ORDER_LIMIT, amount, rate, TIMEINEFFECT_GOOD_TIL_CANCELLED);
	}

	@Override
	public void sell(String coinFrom, String coinTo, Satoshi amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject sell(String coinName, Satoshi amount, Satoshi rate, String conditionType,
			Satoshi conditionTarget) {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject getCurrencies() { // Returns all currencies currently on Bittrex with their metadata
		return getResponse(METHOD_PUBLIC, CURRENCIES, "getcurrencies");
	}

	public JSONObject getWalletHealth() { // Returns wallet health
		return getResponse(METHOD_PUBLIC, CURRENCIES, "getwallethealth");
	}

	public JSONObject getBalanceDistribution(String currency) { // Returns the balance distribution for a specific
																// currency
		return getResponse(METHOD_PUBLIC, CURRENCY, "getbalancedistribution",
				returnCorrectMap("currencyname", currency));
	}

	/**
	 * See deprecated
	 * 
	 * @deprecated this shit is buggy on apis site
	 * @param market
	 * @return
	 */
	@Deprecated
	public JSONObject getMarketSummary(String market) { // Returns a 24-hour summar for a specific market
		return getResponse(METHOD_PUBLIC, MARKET, "getmarketsummary", returnCorrectMap("marketname", market));
	}

	public JSONObject getMarketOrderBook(String market) { // Returns the orderbook for a specific market
		this.API_VERSION = "1.1";
		JSONObject json = getResponse("public", "", "getorderbook", returnCorrectMap("market", market, "type", "both"));
		this.API_VERSION = "2.0";
		return json;
	}

	public JSONObject getMarketHistory(String market) { // Returns latest trades that occurred for a specific market
		return getResponse(METHOD_PUBLIC, MARKET, "getmarkethistory", returnCorrectMap("marketname", market));
	}

	@Deprecated
	public JSONObject getMarkets() { // Returns all markets with their metadata
		return getResponse(METHOD_PUBLIC, MARKETS, "getmarkets");
	}

	public JSONObject getOrder(String orderId) { // Returns information about a specific order (by UUID)
		return getResponse(METHOD_KEY, ORDERS, "getorder", returnCorrectMap("orderid", orderId));
	}

	public JSONObject getOpenOrders() { // Returns all your currently open orders
		return getResponse(METHOD_KEY, ORDERS, "getopenorders");
	}

	public JSONObject getOrderHistory() { // Returns all of your order history
		return getResponse(METHOD_KEY, ORDERS, "getorderhistory");
	}

	public JSONObject cancelOrder(String orderId) { // Cancels a specific order based on its order's UUID.
		return getResponse(METHOD_KEY, MARKET, "tradecancel", returnCorrectMap("orderid", orderId));
	}

	public JSONObject getOpenOrders(String market) { // Returns your currently open orders in a specific market
		return getResponse(METHOD_KEY, MARKET, "getopenorders", returnCorrectMap("marketname", market));
	}

	public JSONObject getOrderHistory(String market) { // Returns your order history in a specific market
		return getResponse(METHOD_KEY, MARKET, "getorderhistory", returnCorrectMap("marketname", market));
	}

	@Override
	public JSONArray getBalances() { // Returns all current balances
		//TODO make it work for v2.0
		return removeZeroBalances(getResponse(METHOD_KEY, BALANCE, "getbalances"));
		
//		this.API_VERSION = "1.1";
//		JSONArray json = removeZeroBalances(getResponse("account", "", "getbalances"));
//		this.API_VERSION = "2.0";
//		
//		return json;
	}

	@Override
	public JSONObject getBalance(String currency) { // Returns the balance of a specific currency
		currency = currency.toUpperCase();
		return getResponse(METHOD_KEY, BALANCE, "getbalance", returnCorrectMap("currencyname", currency));
	}

	public JSONObject getPendingWithdrawals(String currency) { // Returns pending withdrawals for a specific currency
		return getResponse(METHOD_KEY, BALANCE, "getpendingwithdrawals", returnCorrectMap("currencyname", currency));
	}

	public JSONObject getPendingWithdrawals() { // Returns all pending withdrawals
		return getPendingWithdrawals("");
	}

	public JSONObject getWithdrawalHistory(String currency) { // Returns your withdrawal history for a specific currency
		return getResponse(METHOD_KEY, BALANCE, "getwithdrawalhistory", returnCorrectMap("currencyname", currency));
	}

	public JSONObject getWithdrawalHistory() { // Returns your whole withdrawal history
		return getWithdrawalHistory("");
	}

	public JSONObject getPendingDeposits(String currency) { // Returns pending deposits for a specific currency
		return getResponse(METHOD_KEY, BALANCE, "getpendingdeposits", returnCorrectMap("currencyname", currency));
	}

	public JSONObject getPendingDeposits() { // Returns pending deposits for a specific currency
		return getPendingDeposits("");
	}

	public JSONObject getDepositHistory(String currency) { // Returns your deposit history for a specific currency
		return getResponse(METHOD_KEY, BALANCE, "getdeposithistory", returnCorrectMap("currencyname", currency));
	}

	public JSONObject getDepositHistory() { // Returns your whole deposit history
		return getDepositHistory("");
	}

	public JSONObject getDepositAddress(String currency) { // Returns your deposit address for a specific currency
		return getResponse(METHOD_KEY, BALANCE, "getdepositaddress", returnCorrectMap("currencyname", currency));
	}

	public JSONObject generateDepositAddress(String currency) { // Generates a new deposit address for a specific
																// currency
		return getResponse(METHOD_KEY, BALANCE, "generatedepositaddress", returnCorrectMap("currencyname", currency));
	}

	public JSONObject withdraw(String currency, String amount, String address) { // Withdraws a specific amount of a
																					// certain currency to the specified
																					// address
		return getResponse(METHOD_KEY, BALANCE, "withdrawcurrency",
				returnCorrectMap("currencyname", currency, "quantity", amount, "address", address));
	}

	public JSONObject placeOrder(String tradeType, String market, String orderType, Satoshi quantity, Satoshi rate,
			String timeInEffect, String conditionType, String target) { // Places a buy/sell order with these specific
																		// conditions (target only required if a
																		// condition is in place)
		String method = null;
		if (tradeType.equals(TRADE_BUY))
			method = "tradebuy";
		else if (tradeType.equals(TRADE_SELL))
			method = "tradesell";
		return getResponse(METHOD_KEY, MARKET, method,
				returnCorrectMap("marketname", market, "ordertype", orderType, "quantity", String.valueOf(quantity),
						"rate", String.valueOf(rate), "timeineffect", timeInEffect, "conditiontype", conditionType,
						"target", target));
	}

	public JSONObject placeNonConditionalOrder(String tradeType, String market, String orderType, Satoshi quantity,
			Satoshi rate, String timeInEffect) { // Used for non-conditional orders
		return placeOrder(tradeType, market, orderType, quantity, rate, timeInEffect, CONDITION_NONE, "0");
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setKey(String apikey) {
		this.apikey = apikey;
	}

	private HashMap<String, String> returnCorrectMap(String... parameters) { // Handles the exception of the
																				// generateHashMapFromStringList()
																				// method gracefully as to not have an
																				// excess of try-catch statements
		HashMap<String, String> map = null;
		try {
			map = generateHashMapFromStringList(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	private HashMap<String, String> generateHashMapFromStringList(String... strings) throws Exception { // Method to
																										// easily create
																										// a HashMap
																										// from a list
																										// of Strings
		if (strings.length % 2 != 0)
			throw InvalidStringListException;
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < strings.length; i += 2) // Each key will be i, with the following becoming its value
			map.put(strings[i], strings[i + 1]);
		return map;
	}

	private JSONObject getResponse(String type, String methodGroup, String method) {
		return getResponse(type, methodGroup, method, new HashMap<String, String>());
	}

	private JSONObject getResponse(String type, String methodGroup, String method, HashMap<String, String> parameters) {
		int counter = 0;
		while(counter < 10) {
			JSONObject json = getResponseBody(generateUrl(type, methodGroup, method, parameters));
			if( json != null) {
				return json;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter++;
		}
		return null;
	}

	private String generateUrl(String type, String methodGroup, String method, HashMap<String, String> parameters) {
		String url = INITIAL_URL;
		url += "v" + API_VERSION + "/";
		url += type + "/";
		if(!methodGroup.equals(""))
			url += methodGroup + "/";
		url += method;
		url += restHelper.generateUrlParameters(parameters);
		 System.out.println(url);
		return url;
	}

	private JSONObject getResponseBody(String url) {
		boolean publicRequest = true;

		if (!url.substring(url.indexOf("v" + API_VERSION)).contains("/" + METHOD_PUBLIC + "/")) { // Only attach apikey
																									// & nonce if it is
																									// not a public
																									// method
			url += "apikey=" + apikey + "&nonce=" + EncryptionUtility.generateNonce();
			publicRequest = false;
		}

		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			if (!publicRequest)
				request.addHeader("apisign", EncryptionUtility.calculateHash(secret, url, encryptionAlgorithm)); // Attaches
																													// signature
																													// as
																													// a
																													// header
			HttpResponse httpResponse = client.execute(request);

			JSONObject response = null;
			try {
				response = new JSONObject(restHelper.httpResponseToString(httpResponse));
			} catch (JSONException e) {
				SoundUtils.beep();
				System.out.println(e);
			}

			return response;

		} catch (Exception e) {
			e.printStackTrace();
			SoundUtils.beep();
			return null;
		}
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	private JSONArray removeZeroBalances(JSONObject response) {
		JSONArray ja = new JSONArray();
		JSONArray result = response.getJSONArray("result");
		if (result == null) {
			System.out.println(response.getJSONArray("result"));
			SoundUtils.beep();
		}
		for (Object j : result) {
			JSONObject balance = (JSONObject) j;
			balance = balance.getJSONObject("Balance");
			if (balance.getDouble("Balance") != 0) {
				ja.put(j);
			}
		}
		return ja;
	}
}