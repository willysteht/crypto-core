package model.wall;

import utils.Satoshi;

public class SellWall extends Wall{
	private static Type type = Type.Sell;

	public SellWall(Satoshi rate, Satoshi quantity, int positionInOrderBook) {
		super(rate, quantity, type, positionInOrderBook);
	}
}
