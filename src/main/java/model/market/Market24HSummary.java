package model.market;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Satoshi;

public class Market24HSummary {
	private Satoshi high;
	private double baseVolume;
	private Satoshi last;
	private Satoshi low;
	private Satoshi ask;
	private Satoshi bid;
	
	private boolean isVerified;
	
	private String marketName;
	private String marketCurrencyShort;
	private String marketCurrencyLong;
	private double minTradeSize;
	
	
	public Market24HSummary(JSONObject summary, boolean isVerified, JSONObject market) {
		try {
			high = Satoshi.valueOf(summary.getDouble("High"));
			baseVolume = summary.getDouble("BaseVolume");
			last = Satoshi.valueOf(summary.getDouble("Last"));
			low = Satoshi.valueOf(summary.getDouble("Low"));
			ask = Satoshi.valueOf(summary.getDouble("Ask"));
			bid = Satoshi.valueOf(summary.getDouble("Bid"));	
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println(summary);
		}
		
		
		this.isVerified = isVerified;

		marketName = market.getString("MarketName");
		marketCurrencyShort = market.getString("MarketCurrency");
		marketCurrencyLong = market.getString("MarketCurrencyLong");
		minTradeSize = market.getDouble("MinTradeSize");

	}
	
	@Override
	public String toString() {
		String s = "24H Summary" + "\n" +
				"- High: " + high + "\n" +
				"- Low: " + low + "\n" + 
				"- Last: " + last + "\n" + 
				"- Ask: " + ask + "\n" + 
				"- Bid: " + bid + "\n" + 
				"- BaseVolume: " + baseVolume + "\n" + 
				"isVerified: " + isVerified + "\n" +
				"Market: " +  "\n" +
				"- MarketName: " + marketName + "\n" +
				"- MarketCurrencyShort: " + marketCurrencyShort + "\n" +
				"- MarketCurrencyLong: " + marketCurrencyLong + "\n" +
				"- MinTradeSize: " + minTradeSize;
		return s;
	}
	
	public Satoshi getHigh() {
		return high;
	}

	public void setHigh(Satoshi high) {
		this.high = high;
	}

	public double getBaseVolume() {
		return baseVolume;
	}

	public void setBaseVolume(double baseVolume) {
		this.baseVolume = baseVolume;
	}

	public Satoshi getLast() {
		return last;
	}

	public void setLast(Satoshi last) {
		this.last = last;
	}

	public Satoshi getLow() {
		return low;
	}

	public void setLow(Satoshi low) {
		this.low = low;
	}

	public Satoshi getAsk() {
		return ask;
	}

	public void setAsk(Satoshi ask) {
		this.ask = ask;
	}

	public Satoshi getBid() {
		return bid;
	}

	public void setBid(Satoshi bid) {
		this.bid = bid;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getMarketCurrencyShort() {
		return marketCurrencyShort;
	}

	public void setMarketCurrencyShort(String marketCurrencyShort) {
		this.marketCurrencyShort = marketCurrencyShort;
	}

	public String getMarketCurrencyLong() {
		return marketCurrencyLong;
	}

	public void setMarketCurrencyLong(String marketCurrencyLong) {
		this.marketCurrencyLong = marketCurrencyLong;
	}

	public double getMinTradeSize() {
		return minTradeSize;
	}

	public void setMinTradeSize(double minTradeSize) {
		this.minTradeSize = minTradeSize;
	}
}
