package examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import broker.BittrexBroker;
import broker.IBroker;
import broker.helper.BittrexHelper;
import broker.helper.IBrokerHelper;
import model.market.Market;
import model.wall.Wall;
import model.wall.WallScanner;

/**
 * Finds sellwalls on bittrex and prints them to the console.
 * @author willysteht
 *
 */
public class SellwallFinder {
	private static IBroker broker;
	private static IBrokerHelper helper;
	
	private static final int threadcount = 5;

	public static void main(String[] args) throws Exception {
		broker = new BittrexBroker();

		helper = new BittrexHelper();
		helper.setBroker(broker);

		helper.reloadMarketSummaries();
		helper.reloadMyBalances();
		helper.reloadMyMarkets();
		
		Set<Market> marketsIDoNotOwn = helper.getMarketsIDoNotOwn();

		Queue<Market> marketsConsiderTrading = new LinkedBlockingQueue<>();
		for (Market m : marketsIDoNotOwn) {
			boolean hasSomeValue = m.getMarket24hSummary().getLast().isGreaterThanOrEqual(0.0000015);
			boolean hasSomeVolume = m.getMarket24hSummary().getBaseVolume() >= 5;

			if (hasSomeValue && hasSomeVolume) {
				marketsConsiderTrading.add(m);
			}
		}

		List<Market> markets = new ArrayList<>();
		while (marketsConsiderTrading.peek() != null) {
			Market ma = consumeAndLoadOrderbook(marketsConsiderTrading.poll());
			marketsConsiderTrading.add(ma);
			if (marketsConsiderTrading.peek().getOrderBook() != null) {
				markets.add(marketsConsiderTrading.poll());
			}
		}

		markets.sort((m1, m2) -> Double.compare(m2.getMarket24hSummary().getBaseVolume(),
				m1.getMarket24hSummary().getBaseVolume()));
		
		for (Market m : markets) {
			//Which walls should be shown
			//TODO make it not quick and dirty
			Set<Wall> sellWalls = WallScanner.getSellWalls(m);
			for (Wall w : sellWalls) {
				System.out.println(String.format("%s	Vol: %f		%s", m.getMarketName(),
						m.getMarket24hSummary().getBaseVolume(), w));
			}
		}
	}

	private static Market consumeAndLoadOrderbook(Market m) {
		if (Thread.activeCount() <= threadcount) {
			new Thread(() -> m.setOrderBook(helper.loadOrderBook(m.getMarketName())), "ads1").start();
		}
		return m;
	}
}
