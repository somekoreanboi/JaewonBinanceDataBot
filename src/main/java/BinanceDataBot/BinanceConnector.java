package BinanceDataBot;


import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class BinanceConnector {

    private final String symbol;
    private final LinkedBlockingDeque<AggTradeEvent> tradeQueue = new LinkedBlockingDeque<>();
    private final LinkedBlockingDeque<DepthEvent> orderBookQueue = new LinkedBlockingDeque<>();

    public BinanceConnector(String symbol) {
        this.symbol = symbol;
    }

    public LinkedBlockingDeque<AggTradeEvent> getTradeQueue() {
        return this.tradeQueue;
    }

    public LinkedBlockingDeque<DepthEvent> getOrderBookQueue() {
        return this.orderBookQueue;
    }

    public OrderBook getOrderBookSnapshot() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("MT7koIV4yarEUz94cZKmFxOWFnpxzZvElZO2AJ0PpK2UHVl27NWjRwYebLcFDLH6", "5XWhuPUJEUao7yykMfeQnkJmPYYLN6EE2aqZzpSy5ec7j5xCMrFP5EwoUONIhelb");
        BinanceApiRestClient restClient = factory.newRestClient();
        return restClient.getOrderBook(symbol.toUpperCase(), 10);

    }

    public List<AggTrade> getTradeSnapshot() {

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("MT7koIV4yarEUz94cZKmFxOWFnpxzZvElZO2AJ0PpK2UHVl27NWjRwYebLcFDLH6", "5XWhuPUJEUao7yykMfeQnkJmPYYLN6EE2aqZzpSy5ec7j5xCMrFP5EwoUONIhelb");
        BinanceApiRestClient restClient = factory.newRestClient();
        return restClient.getAggTrades(this.symbol.toUpperCase());
    }

    public void startOrderBookEventStreaming(EventManager eventManager, BinanceApiCallback<DepthEvent> callback) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();
        client.onDepthEvent(this.symbol.toLowerCase(), callback);
    }

    public void startAggTradesEventStreaming(EventManager eventManager, BinanceApiCallback<AggTradeEvent> callback) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();
        client.onAggTradeEvent(this.symbol.toLowerCase(), callback);
    }

    public static void main(String[] args) {
    }

}