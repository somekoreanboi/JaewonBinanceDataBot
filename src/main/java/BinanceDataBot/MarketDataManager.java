package BinanceDataBot;

import java.util.concurrent.LinkedBlockingDeque;

public class MarketDataManager implements Runnable {

    protected final BinanceConnector binanceConnector;
    private final OrderBookManager orderBookManager;
    private final TradeManager tradeManager;
    private final EventManager eventManager;


    public MarketDataManager(String symbol, EventManager eventManager) {
        this.binanceConnector = new BinanceConnector(symbol);
        this.orderBookManager = new OrderBookManager(this.binanceConnector, eventManager);
        this.tradeManager = new TradeManager(this.binanceConnector, eventManager);
        this.eventManager = eventManager;
    }


//    public Map<Long, AggTrade> getTradeCache() {
//        return this.tradeManager.getTradeCache();
//    }

    public void subscribeOrderBook() {
        this.orderBookManager.startOrderBookEventStreaming();
    }

    @Override
    public void run() {
        this.subscribeOrderBook();
    }

    public static void main(String args[]) {
    }

}
