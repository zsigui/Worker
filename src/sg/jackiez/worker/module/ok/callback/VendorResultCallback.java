package sg.jackiez.worker.module.ok.callback;

public interface VendorResultCallback {

    void onTradeSuccess();

    void onTradeFail();

    void onCancelOrderSuccess();

    void onCancelOrderFail();
}
