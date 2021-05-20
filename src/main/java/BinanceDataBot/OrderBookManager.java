package BinanceDataBot;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.*;

/**
 * Illustrates how to use the depth event stream to create a local cache of bids/asks for a symbol.
 *
 * Snapshots of the order book can be retrieved from the REST API.
 * Delta changes to the book can be received by subscribing for updates via the web socket API.
 *
 * To ensure no updates are missed, it is important to subscribe for updates on the web socket API
 * _before_ getting the snapshot from the REST API. Done the other way around it is possible to
 * miss one or more updates on the web socket, leaving the local cache in an inconsistent state.
 *
 * Steps:
 * 1. Subscribe to depth events and cache any events that are received.
 * 2. Get a snapshot from the rest endpoint and use it to build your initial depth cache.
 * 3. Apply any cache events that have a final updateId later than the snapshot's update id.
 * 4. Start applying any newly received depth events to the depth cache.
 *
 * The example repeats these steps, on a new web socket, should the web socket connection be lost.
 */
public class OrderBookManager {

    private static final String BIDS = "BIDS";
    private static final String ASKS = "ASKS";

    private final EventManager eventManager;


    private final BinanceDataBot.OrderBook orderBookCache = new BinanceDataBot.OrderBook();
    private final LinkedBlockingDeque<BinanceDataBot.OrderBook> orderBookCacheQueue = new LinkedBlockingDeque<>();

    private long lastUpdateId = -1;
    private final BinanceConnector binanceConnector;

    public OrderBookManager(BinanceConnector binanceConnector, EventManager eventManager) {
        this.binanceConnector = binanceConnector;
        this.eventManager = eventManager;
        this.initialize();
//        this.startOrderBookEventStreaming();
    }

    public void initialize() {
        OrderBook orderBook = this.binanceConnector.getOrderBookSnapshot();

        this.lastUpdateId = orderBook.getLastUpdateId();

        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry ask : orderBook.getAsks()) {
            asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getQty()));
        }
        orderBookCache.put(ASKS, asks);

        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry bid : orderBook.getBids()) {
            bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getQty()));
        }
        orderBookCache.put(BIDS, bids);
    }

    public LinkedBlockingDeque<BinanceDataBot.OrderBook> getOrderBookQueue() {
        return orderBookCacheQueue;
    }

    /**
     * Begins streaming of depth events.
     *
     * Any events received are cached until the rest API is polled for an initial snapshot.
     */
    public void startOrderBookEventStreaming() {
        BinanceApiCallback<DepthEvent> callback;
        callback = new BinanceApiCallback<DepthEvent>() {
            @Override
            public void onResponse(DepthEvent response) {
                if (response.getUpdateId() > lastUpdateId) {
//                    System.out.println(response); //print out for checking
                    lastUpdateId = response.getUpdateId();
                    OrderBookManager.this.updateOrderBook(orderBookCache.getAsks(), response.getAsks());
                    OrderBookManager.this.updateOrderBook(orderBookCache.getBids(), response.getBids());
                    try {
                        eventManager.publish(orderBookCache);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    printDepthCache(); //print out for checking
                }
            }
        };
        this.binanceConnector.startOrderBookEventStreaming(this.eventManager, callback);
    }


    /**
     * Updates an order book (bids or asks) with a delta received from the server.
     *
     * Whenever the qty specified is ZERO, it means the price should was removed from the order book.
     */
    private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries,
                                 List<OrderBookEntry> orderBookDeltas) {
        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                // qty=0 means remove this level
                lastOrderBookEntries.remove(price);
            } else {
                lastOrderBookEntries.put(price, qty);
            }
        }
    }

    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return orderBookCache.getAsks();
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return orderBookCache.getBids();
    }

    /**
     * @return the best ask in the order book
     */
    private Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
        return getAsks().lastEntry();
    }

    /**
     * @return the best bid in the order book
     */
    private Map.Entry<BigDecimal, BigDecimal> getBestBid() {
        return getBids().firstEntry();
    }

    /**
     * @return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
     */
    public BinanceDataBot.OrderBook getOrderBookCache() {
        return orderBookCache;
    }

    /**
     * Prints the cached order book / depth of a symbol as well as the best ask and bid price in the book.
     */
    private void printDepthCache() {
        System.out.println(orderBookCache);
        System.out.println("ASKS:(" + getAsks().size() + ")");
        getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        System.out.println("BIDS:(" + getBids().size() + ")");
        getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        System.out.println("BEST ASK: " + toDepthCacheEntryString(getBestAsk()));
        System.out.println("BEST BID: " + toDepthCacheEntryString(getBestBid()));
    }

    /**
     * Pretty prints an order book entry in the format "price / quantity".
     */
    private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }

    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        new OrderBookManager(new BinanceConnector("neoeth"), eventManager);
    }

}
