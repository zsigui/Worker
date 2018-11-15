package sg.jackiez.worker.module.ok.handler.vendor;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import sg.jackiez.worker.module.ok.OKError;
import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.model.FutureTradeInfo;
import sg.jackiez.worker.module.ok.model.resp.RespBatchCancelTradeV3;
import sg.jackiez.worker.module.ok.model.resp.RespCancelTradeV3;
import sg.jackiez.worker.module.ok.model.resp.RespTradeV3;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.common.CommonUtil;
import sg.jackiez.worker.utils.thread.DefaultThread;

/**
 * 用于进行期货交易及现有资产管理的处理类
 */
public class VendorDataHandler {

    private static final String TAG = "FutureVendor";
    private int mLongLeverage;
    private int mShortLeverage;

    private final Object mLockObj = new Object();
    private Vendor mVendor = new Vendor();
    private Thread mTradeThread;
    private boolean mIsTradeThreadRunning;

    private ConcurrentLinkedQueue<FutureTradeInfo> mTradeInfoList = new ConcurrentLinkedQueue<>();

    public VendorDataHandler(int longLeverage, int shortLeverage) {
        mLongLeverage = longLeverage;
        mShortLeverage = shortLeverage;
    }

    private void addTradeInfoAndNotify(FutureTradeInfo info) {
        if (info == null) {
            return;
        }
        info.clientOId = generateClientOId();
        mTradeInfoList.add(info);
        startTradeThread();
    }

    private String generateClientOId() {
        return String.valueOf(System.currentTimeMillis())
                + String.valueOf((int)(Math.random() * 900 + 100));
    }

    private boolean isTradeThreadAlive() {
        return mTradeThread != null && mTradeThread.isAlive()
                && !mTradeThread.isInterrupted();
    }

    public void startTradeThread() {
        if (mTradeThread != null && mTradeThread.isAlive()
                && !mTradeThread.isInterrupted()) {
            // 已经在跑,直接唤醒即可
            synchronized (mLockObj) {
                mLockObj.notify();
            }
            return;
        }
        mIsTradeThreadRunning = true;
        mTradeThread = new DefaultThread(() -> {

            FutureTradeInfo info;
            long startTime;
            while (mIsTradeThreadRunning) {

                info = mTradeInfoList.poll();
                if (info != null) {
                    startTime = System.currentTimeMillis();
                    if (info.isCancelOp) {
                        if (!CommonUtil.isEmpty(info.orderId)) {
                            handleCancelOrder(info);
                        } else {
                            SLogUtil.i(TAG, "暂不处理多笔订单撤销逻辑");
//                            handleCancelMultiOrder(info);
                        }
                    } else {
                        handleTrade(info);
                    }
                    SLogUtil.i(TAG, "交易请求完成，花费总时间: " + (System.currentTimeMillis() - startTime) + "ms");
                }

                if (mTradeInfoList.isEmpty()) {
                    synchronized (mLockObj) {
                        try {
                            mLockObj.wait();
                        } catch (InterruptedException e) {
                            SLogUtil.v(e);
                        }
                    }
                }
            }
            if (!mTradeInfoList.isEmpty()) {
                SLogUtil.i(TAG, "交易线程暂停，当前尚有未执行操作：" + mTradeInfoList);
            }
        });
        mTradeThread.setPriority(Thread.MAX_PRIORITY);
        mTradeThread.start();
    }

    public void stopTradeThread() {
        if (isTradeThreadAlive()) {
            mIsTradeThreadRunning = false;
            synchronized (mLockObj) {
                mLockObj.notify();
            }
            mTradeThread.interrupt();
        }
    }

    private void handleCancelOrder(FutureTradeInfo info) {
        RespCancelTradeV3 rsp = mVendor.doCancelOrderSync(info.instrumentId, info.orderId);
        SLogUtil.i(TAG, "取消下单操作结果：" + rsp);
        if (OkConfig.IS_TEST) {
            // 测试，直接当成成功处理
            CallbackManager.get().onCancelOrderSuccess(info.orderId, info.instrumentId);
        } else {
            // TODO 简单处理，后续需要重新调整
            if (rsp == null) {
                CallbackManager.get().onCancelOrderFail(OKError.E_NULL, OKError.STR_E_NULL);
            } else {
                // 单笔订单处理
                if (rsp.result) {
                    // 成功
                    CallbackManager.get().onCancelOrderSuccess(info.orderId, info.instrumentId);
                } else {
                    CallbackManager.get().onCancelOrderFail(OKError.E_REQ_FAIL, OKError.STR_E_REQ_FAIL);
                }
            }
        }
    }

//    private void handleCancelMultiOrder(FutureTradeInfo info) {
//        int retryTime;
//        RespBatchCancelTradeV3 rsp = null;
//        retryTime = MAXT_RETRY_TIME;
//        while (rsp == null && retryTime-- > 0) {
//            rsp = doCancelMultiOrder(info.instrumentId, info.orderIds);
//        }
//        if (OkConfig.IS_TEST) {
//            // 测试，直接当成成功处理
//            CallbackManager.get().onCancelOrderSuccess("123456", "123456");
//        } else {
//            // TODO 简单处理，后续需要重新调整
//            if (rsp == null) {
//                CallbackManager.get().onCancelOrderFail(OKError.E_NULL, OKError.STR_E_NULL);
//            } else {
//                // 多笔订单处理
//                if (!rsp.result) {
//                    CallbackManager.get().onCancelOrderFail();
//                } else {
//                    CallbackManager.get().onCancelOrderSuccess(info.orderId, info.instrumentId);
//                }
//            }
//        }
//    }

    private void handleTrade(FutureTradeInfo info) {
        RespTradeV3 rsp = mVendor.doTradeSync(info);
        SLogUtil.i(TAG, "下单操作结果：" + rsp);
        if (OkConfig.IS_TEST) {
            // 测试，直接当成成功处理
            CallbackManager.get().onTradeSuccess(info.clientOId, rsp != null ? rsp.order_id : "testOrderId", info.instrumentId);
        } else {
            // TODO 简单处理，后续需要重新调整
            if (rsp == null || !rsp.result) {
                if (rsp == null) {
                    CallbackManager.get().onTradeFail(OKError.E_NULL, OKError.STR_E_NULL);
                } else {
                    CallbackManager.get().onTradeFail(rsp.error_code, rsp.error_message);
                }
            } else {
                CallbackManager.get().onTradeSuccess(info.instrumentId, rsp.order_id, info.instrumentId);
            }
        }
    }

    private RespTradeV3 doTrade(String instrumentId, double price, long size,
                         byte trendType, String priceType, String clientOId, String leverage) {
        SLogUtil.i(TAG, "执行下单操作：instrumentId = " + instrumentId
         + ", price = " + price + ", size = " + size + ", type = " + trendType
         + ", match_price = " + priceType + ", leverage = " + leverage + ", clientOId = " + clientOId);
        return JsonUtil.jsonToSuccessDataForFutureWithErrCode(FutureRestApiV3.doTrade(instrumentId,
                String.valueOf(trendType), String.valueOf(price),
                String.valueOf(size), priceType, leverage, clientOId),
                new TypeReference<RespTradeV3>() {});
    }

    private RespBatchCancelTradeV3 doCancelMultiOrder(String instrumentId, List<String> orderIds) {
        return JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.doBatchCancelTrade(instrumentId, JsonUtil.objToJson(orderIds)),
                new TypeReference<RespBatchCancelTradeV3>() {});
    }

    public void buyShort(String instrumentId, double price, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mShortLeverage));
    }

    public void buyShortDirectly(String instrumentId, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mShortLeverage));
    }

    public void sellShort(String instrumentId, double price, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_SELL_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mShortLeverage));
    }

    public void sellShortDirectly(String instrumentId, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE, mShortLeverage));
    }

    public void buyLong(String instrumentId, double price, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mLongLeverage));
    }

    public void buyLongDirectly(String instrumentId, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE, mLongLeverage));
    }

    public void sellLong(String instrumentId, double price, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mLongLeverage));
    }

    public void sellLongDirectly(String instrumentId, long amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE, mLongLeverage));
    }

    public void cancelOrder(String instrumentId, String orderId) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, orderId));
    }

    public void cancelOrders(String instrumentId, List<String> orderIds) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, orderIds));
    }
}
