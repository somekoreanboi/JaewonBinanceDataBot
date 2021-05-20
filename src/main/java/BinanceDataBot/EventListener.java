package BinanceDataBot;


public interface EventListener {
    void handleEvent(OrderBook orderBook) throws InterruptedException;
    void handleEvent(ScheduleEvent timer) throws InterruptedException;
}
