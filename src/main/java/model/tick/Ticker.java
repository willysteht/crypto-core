package model.tick;

import utils.Satoshi;

public class Ticker {
	private Satoshi bid, ask, last;

	public Ticker(Satoshi bid, Satoshi ask, Satoshi last) {
		super();
		this.bid = bid;
		this.ask = ask;
		this.last = last;
	}

	public Satoshi getBid() {
		return bid;
	}

	public Satoshi getAsk() {
		return ask;
	}

	public Satoshi getLast() {
		return last;
	}
	
	

}
