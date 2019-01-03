package model.bar;

import java.util.Date;

import utils.Satoshi;

public class BittrexBar{
	private Satoshi open, high, low, close, volume;
	private Date date;
	
	public BittrexBar(double close, Date date, double volume, double high, double low, double open) {
		this(Satoshi.valueOf(close),
				date,
                Satoshi.valueOf(volume),
                Satoshi.valueOf(high),
                Satoshi.valueOf(low),
                Satoshi.valueOf(open));
	}
	

	public BittrexBar(Satoshi close, Date date, Satoshi volume, Satoshi high, Satoshi low, Satoshi open) {
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.date = date;
	}

	public Satoshi getOpen() {
		return open;
	}

	public Satoshi getLow() {
		return low;
	}

	public Satoshi getHigh() {
		return high;
	}

	public Satoshi getClose() {
		return close;
	}

	public Satoshi getVolume() {
		return volume;
	}

	public Date getDate() {
		return date;
	}


	/**
	 * close - open
	 * @return
	 */
	public Satoshi calcDiff() {
		return close.minus(open);
	}
}
