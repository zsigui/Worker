package sg.jackiez.worker.module.ok.handler;

import java.util.List;

import sg.jackiez.worker.module.ok.model.resp.RespCancelTrade;

interface IVendor {

    void buyShort(String symbol, double price, double amount);

    void buyShortDirectly(String symbol, double amount);

    void sellShort(String symbol, double price, double amount);

    void sellShortDirectly(String symbol, double amount);

    void buyLong(String symbol, double price, double amount);

    void buyLongDirectly(String symbol, double amount);

    void sellLong(String symbol, double price, double amount);

    void sellLongDirectly(String symbol, double amount);

    RespCancelTrade cancelOrder(String symbol, String orderId);

    RespCancelTrade cancelOrders(String symbol, List<String> orderIds);
}
