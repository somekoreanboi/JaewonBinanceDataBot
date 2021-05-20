package BinanceDataBot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadBuilder {

    private final EventManager eventManager;
    private final MarketDataManager marketDataManager;
    private final AnalyticManager analyticManager;

    public MultiThreadBuilder (EventManager eventManager, MarketDataManager marketDataManager, AnalyticManager analyticManager) {
        this.eventManager = eventManager;
        this.marketDataManager = marketDataManager;
        this.analyticManager = analyticManager;
    }

    public void execute() {
        // Executor service for multithreading
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        threadPool.execute(marketDataManager);
        threadPool.execute(analyticManager);
    }
}
