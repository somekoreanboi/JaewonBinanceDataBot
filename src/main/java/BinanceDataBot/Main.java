package BinanceDataBot;

import org.quartz.SchedulerException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws SchedulerException {
        // Create Managers
        EventManager eventManager = new EventManager();
        MarketDataManager marketDataManager = new MarketDataManager("btcusdt", eventManager);
        SchedulerManager scheduleManager = new SchedulerManager(eventManager);
        AnalyticManager analyticManager = new AnalyticManager(5, 10, eventManager, scheduleManager);

        new MultiThreadBuilder(eventManager, marketDataManager, analyticManager).execute();

    }
}
