package BinanceDataBot;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.util.Map;
import java.util.NavigableMap;

public class SimpleMovingAverage implements MovingAverage{

    DescriptiveStatistics dataBasket;
    private Long lastOrderBookId = 0L;

    public SimpleMovingAverage(int basketSize) {
        dataBasket = new DescriptiveStatistics(basketSize);
    }

    private double getWeightedMid(OrderBook orderbook) {
        double bestBidPrice = orderbook.getBestBid().getKey().doubleValue();
        double bestBidQuantity = orderbook.getBestBid().getValue().doubleValue();
        double bestBidTotal = bestBidPrice * bestBidQuantity;
        double bestAskPrice = orderbook.getBestAsk().getKey().doubleValue();
        double bestAskQuantity = orderbook.getBestAsk().getValue().doubleValue();
        double bestAskTotal = bestAskPrice * bestAskQuantity;
        double totalQuantity = bestBidQuantity + bestAskQuantity;
        double weightedMid = (bestBidTotal + bestAskTotal) / totalQuantity;
        return weightedMid;
    }


    public void updateRecentPrices(NavigableMap<Long, OrderBook> orderBookCache) {
        Map<Long, OrderBook> recentOrderBooks = orderBookCache.tailMap(lastOrderBookId);
        lastOrderBookId = orderBookCache.lastKey();
        recentOrderBooks.forEach((key, value) -> dataBasket.addValue((getWeightedMid(value))));
    }

    public Double computeSMU() {
        int windowSize = this.dataBasket.getWindowSize();
        if (this.dataBasket.getN() != windowSize) {
            System.out.println("Please wait for more data to be proceeded");
            return -1.0;
        }
        System.out.println("SMA under the window size " + windowSize + " is: " + this.dataBasket.getMean());
        return this.dataBasket.getMean();
    }

    public static void main(String[] args) {
        DescriptiveStatistics stats = new DescriptiveStatistics(3);
//        stats.addValue(1);
//        System.out.println(stats.getMean());
//        stats.addValue(2);
//        System.out.println(stats.getMean());
//        stats.addValue(3);
//        System.out.println(stats.getMean());
//        stats.addValue(4);
//        System.out.println(stats.getMean());
//        stats.addValue(8);
        System.out.println(stats.getMean());
    }
}