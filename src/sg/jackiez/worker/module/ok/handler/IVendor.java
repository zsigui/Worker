package sg.jackiez.worker.module.ok.handler;

interface IVendor {

    void buyShort(String symbol, double price, double amount);

    void buyShortDirectly(String symbol, double amount);

    void sellShort(String symbol, double price, double amount);

    void sellShortDirectly(String symbol, double amount);

    void buyLong(String symbol, double price, double amount);

    void buyLongDirectly(String symbol, double amount);

    void sellLong(String symbol, double price, double amount);

    void sellLongDirectly(String symbol, double amount);
}
