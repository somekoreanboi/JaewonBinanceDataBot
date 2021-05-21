package BinanceDataBotTest;

import BinanceDataBot.BinanceConnector;
import BinanceDataBot.EventManager;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class BinanceConnectorTest {

    EventManager eventManager = new EventManager();
    BinanceConnector binanceConnector = new BinanceConnector("neoeth");

    //Test out simple classes first; like SMA

    @Test
    public void testGetOrderBookSnapshot() {
        assertNotNull(binanceConnector.getOrderBookSnapshot());
    }

    @Test
    public void testGetTradeSnapshot() {
        assertNotNull(binanceConnector.getTradeSnapshot());
    }

    //Other two methods shall be tested under OrderBookManagerTest class.

}
