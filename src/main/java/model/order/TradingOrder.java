package model.order;

import utils.Satoshi;

public class TradingOrder {
	public static final String ORDER_TYPE_LIMIT_SELL = "LIMIT_SELL";
	public static final String ORDER_TYPE_LIMIT_BUY = "LIMIT_BUY";
	
	private String orderType; // buy, sell,
	private Satoshi quantity;
	private Satoshi rate;
	private String orderId;
	private String name;
	private Satoshi cost;

	public TradingOrder(String name, String orderType, Satoshi quantity, Satoshi rate, String orderId, Satoshi cost) {
		this.orderType = orderType;
		this.quantity = quantity;
		this.rate = rate;
		this.orderId = orderId;
		this.name = name;
		this.cost = cost;
	}


	public Satoshi getQuantity() {
		return quantity;
	}

	/**
	 * see Constants
	 * @return
	 */
	public String getOrderType() {
		return orderType;
	}

	public Satoshi getRate() {
		return rate;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getName() {
		return name;
	}
	
	public Satoshi getCost() {
		return cost;
	}
}
