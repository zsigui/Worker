package sg.jackiez.worker.module.ok.handler.vendor;

import java.util.List;

interface IVendor {

    void buyShort(String instrumentId, double price, long amount);

    void buyShortDirectly(String instrumentId, long amount);

    void sellShort(String instrumentId, double price, long amount);

    void sellShortDirectly(String instrumentId, long amount);

    void buyLong(String instrumentId, double price, long amount);

    void buyLongDirectly(String instrumentId, long amount);

    void sellLong(String instrumentId, double price, long amount);

    void sellLongDirectly(String instrumentId, long amount);

    void cancelOrder(String instrumentId, String orderId);

    void cancelOrders(String instrumentId, List<String> orderIds);
}
