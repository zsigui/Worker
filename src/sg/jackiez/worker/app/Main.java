package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.VendorResultCallback;
import sg.jackiez.worker.module.ok.handler.AccountDataGrabber;
import sg.jackiez.worker.module.ok.handler.vendor.FutureVendorV3;
import sg.jackiez.worker.module.ok.manager.PrecursorManager;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        SLogUtil.setPrintFile(true);
        SLogUtil.setDebugLevel(SLogUtil.Level.DEBUG);
        PrecursorManager.get().init(OKTypeConfig.SYMBOL_EOS, OKTypeConfig.CONTRACT_TYPE_QUARTER);
        FutureVendorV3 vendorV3 = new FutureVendorV3(PrecursorManager.get().getLongLeverage(),
                PrecursorManager.get().getShortLeverage());
        vendorV3.startTradeThread();
        vendorV3.buyLong(PrecursorManager.get().getInstrumentId(), 5.1, 1);
//        SLogUtil.i(FutureRestApiV3.doCancelTrade("1541147388914581", PrecursorManager.get().getInstrumentId()));
//        SLogUtil.i(FutureRestApiV3.doTrade(PrecursorManager.get().getInstrumentId(), "1",
//                "5", "1", "0", "20", "1541145434041606"));
        CallbackManager.get().addVendorResultCallback(new VendorResultCallback() {
            @Override
            public void onTradeSuccess(String clientOId, String orderId, String instrumentId) {
                long starTime = System.currentTimeMillis();
                new Thread(() -> {
                    AccountDataGrabber accountDataGrabber = new AccountDataGrabber();
                    boolean hasOrderId = false;
                    while (!hasOrderId) {
                        hasOrderId = (accountDataGrabber.getOrderIfContainByOrderId(instrumentId,
                                orderId, OKTypeConfig.STATUS_NOT_TRANSACT) != null);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    SLogUtil.i("找到可取消订单，总耗时: " + (System.currentTimeMillis() - starTime) + " ms");
                    vendorV3.cancelOrder(instrumentId, orderId);
                }).start();
            }

            @Override
            public void onTradeFail(int errCode, String errMsg) {

            }

            @Override
            public void onCancelOrderSuccess(String orderId, String instrumentId) {
                long starTime = System.currentTimeMillis();
                new Thread(() -> {
                    AccountDataGrabber accountDataGrabber = new AccountDataGrabber();
                    boolean hasOrderId = false;
                    while (!hasOrderId) {
                        hasOrderId = (accountDataGrabber.getOrderIfContainByOrderId(instrumentId,
                                orderId, OKTypeConfig.STATUS_CANCELED) != null);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    SLogUtil.i("找到已取消订单，总耗时: " + (System.currentTimeMillis() - starTime) + " ms");
                    vendorV3.stopTradeThread();
                }).start();
            }

            @Override
            public void onCancelOrderFail(int errCode, String errMsg) {
                vendorV3.stopTradeThread();
            }
        });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        AccountDataGrabber accountDataGrabber = new AccountDataGrabber();
//        accountDataGrabber.getTotalOrderList(PrecursorManager.get().getInstrumentId());
//        vendorV3.stopTradeThread();
//        FutureDataGrabber grabber = new FutureDataGrabber(PrecursorManager.get().getInstrumentId());
//        grabber.startAll();
//        DBManager.get().startGrab();

    }

}
