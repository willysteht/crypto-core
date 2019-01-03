package model.order;

import utils.Satoshi;

public class OrderBookOrder implements Comparable<OrderBookOrder> {
	private Satoshi rate;
	private Satoshi quantity;
	private Type type;
	private Satoshi total;
	
	public enum Type {
		Sell, Buy;
	}

	public OrderBookOrder(Satoshi rate, Satoshi quantity, Type type) {
		this.rate = rate;
		this.quantity = quantity;
		this.type = type;

		this.total = rate.multipliedBy(quantity);
	}
	
	public Satoshi getRate() {
		return rate;
	}

	public Satoshi getQuantity() {
		return quantity;
	}

	public Type getType() {
		return type;
	}

	public Satoshi getTotal() {
		return total;
	}

	@Override
	public String toString() {
		return "Rate: " + rate +
				" Quantity: " + quantity +
				" Total: " + total;
	}
	@Override
	public int compareTo(OrderBookOrder o) {
		if(this.type == Type.Buy)
			return Double.compare(o.rate.doubleValue(), this.rate.doubleValue());
		
		return Double.compare( this.rate.doubleValue(), o.rate.doubleValue());
	}

}
