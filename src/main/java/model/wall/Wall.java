package model.wall;

import model.order.OrderBookOrder;
import utils.Satoshi;

public abstract class Wall extends OrderBookOrder{
	private int positionInOrderBook;

	public Wall(Satoshi rate, Satoshi quantity, Type type, int positionInOrderBook) {
		super(rate, quantity, type);
		this.positionInOrderBook = positionInOrderBook;
	}

	public int getPositionInOrderBook() {
		return positionInOrderBook;
	}
	
	@Override
	public String toString() {
		if(getType().equals(Type.Sell)) {
			return "SellWall bei: " + getRate() + " Größe: " + getQuantity() + " BTC Pos: " + getPositionInOrderBook();
		} else {
			return "BuyWall bei: " + getRate() + " Größe: " + getQuantity() + " BTC Pos: " + getPositionInOrderBook();
		}
			
		
	}
}
