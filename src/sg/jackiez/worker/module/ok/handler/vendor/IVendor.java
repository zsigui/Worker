package sg.jackiez.worker.module.ok.handler.vendor;

import java.util.List;

interface IVendor {

    void buyShort(String instrumentId, double price, double amount);

    void buyShortDirectly(String instrumentId, double amount);

    void sellShort(String instrumentId, double price, double amount);

    void sellShortDirectly(String instrumentId, double amount);

    void buyLong(String instrumentId, double price, double amount);

    void buyLongDirectly(String instrumentId, double amount);

    void sellLong(String instrumentId, double price, double amount);

    void sellLongDirectly(String instrumentId, double amount);

    void cancelOrder(String instrumentId, String orderId);

    void cancelOrders(String instrumentId, List<String> orderIds);
}
