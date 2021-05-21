package BinanceDataBotTest;

import BinanceDataBot.BinanceConnector;
import BinanceDataBot.EventManager;
import BinanceDataBot.TradeManager;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.market.AggTrade;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;

public class TradeManagerTest {

    BinanceConnector binanceConnector;
    TradeManager tradeManager;
    EventManager eventManager;

    public TradeManagerTest() {
        this.binanceConnector = new BinanceConnector("neoeth");
        this.eventManager = new EventManager();
        this.tradeManager = new TradeManager(binanceConnector, eventManager);
    }

    @Test
    public void testInitialize() throws InterruptedException {
        Map<Long, AggTrade> oldCache = this.tradeManager.getTradeCache();
        this.tradeManager.initialize();
        Map<Long, AggTrade> newCache = this.tradeManager.getTradeCache();
        assertNotSame(oldCache, newCache);
    }

    private void testStartAggTradesEventStreaming() {

    }
}
