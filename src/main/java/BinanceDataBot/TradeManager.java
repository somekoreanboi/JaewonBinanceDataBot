package BinanceDataBot;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TradeManager {
    BinanceConnector binanceConnector;
    EventManager eventManager;

    public Map<Long, AggTrade> getTradeCache() {
        return tradeCache;
    }

    private final Map<Long, AggTrade> tradeCache = new ConcurrentHashMap<>();

    public TradeManager(BinanceConnector binanceConnector, EventManager eventManager) {
        this.binanceConnector = binanceConnector;
        this.eventManager = eventManager;
        this.initialize();
        this.startAggTradesEventStreaming();
    }

    public void initialize() {
        List<AggTrade> aggTrades = this.binanceConnector.getTradeSnapshot();
        for (AggTrade aggTrade : aggTrades) {
            tradeCache.put(aggTrade.getAggregatedTradeId(), aggTrade);
        }
    }

    private void startAggTradesEventStreaming() {
        BinanceApiCallback<AggTradeEvent> callback;
        callback =
                new BinanceApiCallback<AggTradeEvent>() {
                    @Override
                    public void onResponse(AggTradeEvent response) {
                        Long aggregatedTradeId = response.getAggregatedTradeId();
                        AggTrade updateAggTrade = tradeCache.get(aggregatedTradeId);
                        if (updateAggTrade == null) {
                            // new agg trade
                            updateAggTrade = new AggTrade();
                        }
                        updateAggTrade.setAggregatedTradeId(aggregatedTradeId);
                        updateAggTrade.setPrice(response.getPrice());
                        updateAggTrade.setQuantity(response.getQuantity());
                        updateAggTrade.setFirstBreakdownTradeId(response.getFirstBreakdownTradeId());
                        updateAggTrade.setLastBreakdownTradeId(response.getLastBreakdownTradeId());
                        updateAggTrade.setBuyerMaker(response.isBuyerMaker());

                        // Store the updated agg trade in the cache
                        tradeCache.put(aggregatedTradeId, updateAggTrade);
//                        System.out.println(updateAggTrade);

                        // push to eventmanager
                        try {
                            eventManager.publish(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
        this.binanceConnector.startAggTradesEventStreaming(this.eventManager, callback);
    }
    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        new TradeManager(new BinanceConnector("btcusdt"), eventManager);
    }
}

