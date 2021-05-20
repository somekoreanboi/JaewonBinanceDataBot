package BinanceDataBot;

import com.binance.api.client.domain.event.AggTradeEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventManager {

    private final BlockingQueue<AggTradeEvent> tradeQueue = new LinkedBlockingQueue<>();

    private final BlockingQueue<OrderBook> orderBookQueue = new LinkedBlockingQueue<>();

    private final BlockingQueue<ScheduleEvent> scheduleQueue = new LinkedBlockingQueue<>();

    public EventManager() {
    }

    public void publish(OrderBook orderBook) throws InterruptedException {
        orderBookQueue.put(orderBook);
    }

    public void publish(AggTradeEvent tradeEvent) throws InterruptedException {
        tradeQueue.put(tradeEvent);
    }

    public void publish(ScheduleEvent scheduleEvent) throws InterruptedException {
        scheduleQueue.put(scheduleEvent);
    }

    public OrderBook takeOrderBookQueue() throws InterruptedException {
        return orderBookQueue.take();
    }

    public AggTradeEvent takeTradeQueue() throws InterruptedException {
        return tradeQueue.take();
    }

    public ScheduleEvent takeScheduleQueue() throws InterruptedException {
        return scheduleQueue.take();
    }

    public BlockingQueue<AggTradeEvent> getTradeQueue() {
        return tradeQueue;
    }

    public BlockingQueue<OrderBook> getOrderBookQueue() {
        return orderBookQueue;
    }

    public BlockingQueue<ScheduleEvent> getScheduleQueue() {
        return scheduleQueue;
    }

}
