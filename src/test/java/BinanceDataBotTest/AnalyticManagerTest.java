package BinanceDataBotTest;

import BinanceDataBot.*;
import com.binance.api.client.domain.account.Order;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.w3c.dom.events.Event;

import java.util.NavigableMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AnalyticManagerTest {

    AnalyticManager analyticManager;
    EventManager eventManager;
    SchedulerManager schedulerManager;

    public AnalyticManagerTest() throws SchedulerException {
        this.eventManager = new EventManager();
        this.schedulerManager = new SchedulerManager(eventManager);
        this.analyticManager = new AnalyticManager(5, 10, eventManager, schedulerManager);
    }

    @Test
    public void testOrderBookHandleEvent() throws InterruptedException {
        OrderBook orderBook = new OrderBook();
        this.analyticManager.handleEvent(orderBook);
        assertNotNull(this.analyticManager.getOrderBookCache().firstEntry());
    }

    @Test
    public void testScheduleEventHandleEvent() throws InterruptedException {
        OrderBook orderBookCache = (OrderBook) this.analyticManager.getOrderBookCache();
        ScheduleEvent timer = new ScheduleEvent("sma1");
        this.analyticManager.handleEvent(timer);
        //Incomplete Yet
    }

    @Test
    public void testGetOrderBookCache() {
        assertNotNull(this.analyticManager.getOrderBookCache());
    }
}
