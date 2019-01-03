package model.order;

import java.util.Set;
import java.util.TreeSet;

public class OrderBook {
	private Set<OrderBookOrder> buyOrders;
	private Set<OrderBookOrder> sellOrders;
	
	public OrderBook() {
		buyOrders = new TreeSet<>();
		sellOrders = new TreeSet<>();
	}
	
	public Set<OrderBookOrder> getBuyOrders() {
		return buyOrders;
	}
	public void setBuyOrders(Set<OrderBookOrder> buyOrders) {
		this.buyOrders = buyOrders;
	}
	public Set<OrderBookOrder> getSellOrders() {
		return sellOrders;
	}
	public void setSellOrders(Set<OrderBookOrder> sellOrders) {
		this.sellOrders = sellOrders;
	}
}