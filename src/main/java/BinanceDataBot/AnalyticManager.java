package BinanceDataBot;

import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.quartz.SchedulerException;

import java.util.NavigableMap;
import java.util.TreeMap;

public class AnalyticManager implements EventListener, Runnable {
    private final SimpleMovingAverage sma1;
    private final SimpleMovingAverage sma2;
    private final EventManager eventManager;
    private final NavigableMap<Long, OrderBook> orderBookCache = new TreeMap<>();
    private long orderBookId = 0L;
//    private SignalGenerator signalGenerator;
    private final SchedulerManager schedulerManager;

    public AnalyticManager(int window1, int window2, EventManager eventManager, SchedulerManager schedulerManager) {
        sma1 = new SimpleMovingAverage(window1);
        sma2 = new SimpleMovingAverage(window2);
        this.eventManager = eventManager;
        this.schedulerManager = schedulerManager;
//        signalGenerator = new SignalGenerator(sma1, sma2, this);
    }

    private void initializeCallback(int intervalMillis) {
        try {
            schedulerManager.periodicCallBack(intervalMillis, "sma1");
            schedulerManager.periodicCallBack(intervalMillis, "sma2");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void handleEvent(OrderBook orderBook) throws InterruptedException {
        orderBookCache.put(orderBookId++, orderBook);
    }

    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
        if (timer.getTag().equals("sma1")) {
            sma1.updateRecentPrices(orderBookCache);
        }

        if (timer.getTag().equals("sma2")) {
            sma2.updateRecentPrices(orderBookCache);
        }
            sma1.computeSMU();
            sma2.computeSMU();
//        signalGenerator.generateSignal();
    }

    public NavigableMap<Long, OrderBook> getOrderBookCache() {
        return this.orderBookCache;
    }


    @Override
    public void run() {
        initializeCallback(2000);
        while (true) {
            try {
                //What if we have two managers getting the data from the same queue?
                handleEvent(this.eventManager.takeOrderBookQueue());
                handleEvent(this.eventManager.takeScheduleQueue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}