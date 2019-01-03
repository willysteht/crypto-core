package model.market;

import model.order.OrderBook;
import utils.Satoshi;

public class Market {
	private final String marketName;

	private Satoshi balance;
	private Satoshi bitcoinBalance;
	private Market24HSummary market24hSummary;
	private boolean isInWallet;
	private OrderBook orderBook;
	private Builder builder;

	private Market(Builder builder) {
		this.builder = builder;
		this.marketName = builder.marketName;
		this.balance = builder.balance;
		this.market24hSummary = builder.market24hSummary;
		this.bitcoinBalance = builder.bitcoinBalance;
		
		this.isInWallet = false;
		this.orderBook = null;
	}
	
	public Builder getBuilder() {
		return this.builder;
	}
	
	public OrderBook getOrderBook() {
		return orderBook;
	}
	
	public boolean isInWallet() {
		return isInWallet = balance.isGreaterThan(0);
	}

	public String getMarketName() {
		return marketName;
	}

	public Satoshi getBalance() {
		return balance;
	}
	
	public Satoshi getBitcoinBalance() {
		return bitcoinBalance;
	}

	public Market24HSummary getMarket24hSummary() {
		return market24hSummary;
	}
	
	public void setOrderBook(OrderBook orderBook) {
		this.orderBook = orderBook;
	}

	@Override
	public String toString() {
		return "Name: " + marketName + "	Available: " + balance + "	= " + bitcoinBalance + "BTC";
	}

	public static class Builder {
		private String marketName;
		private Satoshi balance;
		private Satoshi bitcoinBalance;
		private Market24HSummary market24hSummary;

		public Builder setMarketName(String marketName) {
			this.marketName = marketName;
			return this;
		}

		public Builder setBalance(Satoshi balance) {
			this.balance = balance;
			return this;
		}
		
		public Builder setBitcoinBalance(Satoshi bitcoinBalance) {
			this.bitcoinBalance = bitcoinBalance;
			return this;
		}

		public Builder setMarket24hSummary(Market24HSummary market24hSummary) {
			this.market24hSummary = market24hSummary;
			return this;
		}
		
		

		public Market build() {
			return new Market(this);
		}
	}

	

}
