package broker;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Interval;
import utils.Satoshi;

public interface IBroker {
	
	public static final String balanceTotal = "Total";
	public static final String balanceFree = "Free";
	public static final String balanceInTransaction = "InTransaction";
	
	/**
	 * Establish connection to your broker 
	 * Delivers your API-Key
	 */
	public void connect();
	/**
	 * Closes connection to your broker
	 */
	public void disconnect();
	/**
	 * Buys a certain amount of coin from Bitcoin
	 * @param coin: Coin you want to have
	 * @param amount: How much coin you want to buy
	 * @param rate: At wich rate do you want to sell
	 * @return confirmation as JSONArray
	 */
	public JSONObject buy(String coin, Satoshi amount, Satoshi rate);
	/**
	 * Buys a certain amount of coinTo from coinFrom
	 * @param coinTo: Coin you want to have
	 * @param coinFrom: Coin you have
	 * @param num: How much coin you want to buy
	 */
	public void buy(String coinTo, String coinFrom, Satoshi amount);
	/**
	 * Trades a coin into Bitcoin by a certain amount
	 * @param coin: Coin you have
	 * @param amount: How much coin do you want to sell
	 * @param rate: At which rate you want to sell
	 */
	public JSONObject sell(String coin, Satoshi amount, Satoshi sellPrice);
	/**
	 * Trades one Coin to another Coin by a certain amount
	 * @param coinFrom: Coin you have
	 * @param coinTo: Coin you want to get
	 * @param amount: How much coin do you want to sell
	 */
	public void sell(String coinFrom, String coinTo, Satoshi amount);
	/**
	 * Sells a coin if a specific condition was met.
	 * @param coinName
	 * @param amount
	 * @param rate
	 * @param conditionType - the condition
	 * @param conditionTarget - price when the condition should hit
	 * @return
	 */
	JSONObject sell(String coinName, Satoshi amount, Satoshi rate, String conditionType, Satoshi conditionTarget);
	/**
	 * Return the balance of the account as JSONArray
	 * @return 
	 */
	public JSONArray getBalances();
	/**
	 * Returns the balance of a given coin as JSONObject
	 * @param coinName
	 * @return
	 */
	public JSONObject getBalance(String coinName);
	
	/**
	 * Gets the candels of a coin
	 * 
	 * @param coin - the coin
	 * @param interval 
	 * @return
	 */
	public JSONObject getTicker(String coin, Interval interval);
	public JSONObject cancelOrder(String orderId);
	public JSONObject getMarketOrderBook(String coin);
	public JSONObject getTicker(String market);
}
