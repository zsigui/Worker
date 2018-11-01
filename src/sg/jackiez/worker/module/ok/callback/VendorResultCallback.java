package sg.jackiez.worker.module.ok.callback;

public interface VendorResultCallback {

    void onTradeSuccess(String clientOId, String orderId, String instrumentId);

    void onTradeFail(int errCode, String errMsg);

    void onCancelOrderSuccess(String orderId, String instrumentId);

    void onCancelOrderFail(int errCode, String errMsg);
}
