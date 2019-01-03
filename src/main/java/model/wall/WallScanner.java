package model.wall;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import model.market.Market;
import model.order.OrderBookOrder;
import utils.Satoshi;

public class WallScanner {
	Set<Wall> walls;
	
	public WallScanner() {
		walls = new TreeSet<>();
	}

	public static Set<Wall> getSellWalls(Market m) {
		Set<Wall> out = new LinkedHashSet<>();
		
		double volume = m.getMarket24hSummary().getBaseVolume();
		int i = 0;
		for (OrderBookOrder sellOrder : m.getOrderBook().getSellOrders()) {
			++i;
			if(i >= 10) break;
			
			double wallSize = 100;
			if(volume > 400d) {
				wallSize = 10;
			} else if (volume > 200d) {
				wallSize = 6;
			} else if (volume > 100d) {
				wallSize = 5;
			} else if (volume > 70d) {
				wallSize = 4;
			} else if (volume > 30d) {
				wallSize = 2;
			} else if (volume > 5d) {
				wallSize = 1;
			}
			
			Satoshi sellOrderSize = sellOrder.getTotal(); 
			if(sellOrderSize.isGreaterThan(wallSize)) {
				Wall sellwall = new SellWall(sellOrder.getRate(), sellOrderSize, i);
				out.add(sellwall);
			}
		}
		
		return out;
	}
}
