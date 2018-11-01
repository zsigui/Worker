package sg.jackiez.worker.module.ok.handler.vendor;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import sg.jackiez.worker.module.ok.OKError;
import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.module.ok.model.base.BaseM;
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
public class FutureVendorV3 implements IVendor{

    private static final String TAG = "FutureVendor";

    private static final int MAXT_RETRY_TIME = 3;

    private int mLongLeverage;
    private int mShortLeverage;

    private final Object mLockObj = new Object();
    private Thread mTradeThread;
    private boolean mIsTradeThreadRunning;

    private ConcurrentLinkedQueue<FutureTradeInfo> mTradeInfoList = new ConcurrentLinkedQueue<>();

    public FutureVendorV3(int longLeverage, int shortLeverage) {
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
                        if (CommonUtil.isEmpty(info.orderId)) {
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
        int retryTime;
        RespCancelTradeV3 rsp = null;
        retryTime = MAXT_RETRY_TIME;
        while (rsp == null && retryTime-- > 0) {
            rsp = doCancelOrder(info.instrumentId, info.orderId);
        }
        if (OkConfig.IS_TEST) {
            // 测试，直接当成成功处理
            CallbackManager.get().onCancelOrderSuccess("123456", "123456");
        } else {
            // TODO 简单处理，后续需要重新调整
            if (rsp == null) {
                CallbackManager.get().onCancelOrderFail(OKError.E_NULL, OKError.STR_E_NULL);
            } else {
                // 单笔订单处理
                if (rsp.result) {
                    // 成功
                    DBManager.get().updateCancelTradeState(info.instrumentId,
                            info.orderId, OKTypeConfig.DB_STATE_CANCELLING);
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
        int retryTime;
        RespTradeV3 rsp = null;
        retryTime = MAXT_RETRY_TIME;
        DBManager.get().saveTrade(info, OKTypeConfig.DB_STATE_INIT);
        while (rsp == null && retryTime-- > 0) {
            rsp = doTrade(info.instrumentId, info.price, info.amount, info.trendType,
                    info.priceType, info.clientOId, String.valueOf(info.leverage));
        }
        if (OkConfig.IS_TEST) {
            // 测试，直接当成成功处理
            CallbackManager.get().onTradeSuccess("1234123","1234123412", "EOS-USD-181228");
        } else {
            // TODO 简单处理，后续需要重新调整
            if (rsp == null || !rsp.result) {
                if (rsp == null) {
                    CallbackManager.get().onTradeFail(OKError.E_NULL, OKError.STR_E_NULL);
                } else {
                    CallbackManager.get().onTradeFail(rsp.error_code, rsp.error_message);
                }
            } else {
                DBManager.get().updateTradeState(info.clientOId, rsp.order_id, OKTypeConfig.DB_STATE_TRADING);
                CallbackManager.get().onTradeSuccess(info.instrumentId, rsp.order_id, info.instrumentId);
            }
        }
    }

    private RespTradeV3 doTrade(String instrumentId, double price, double size,
                         byte trendType, String priceType, String clientOId, String leverage) {
        return JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.doTrade(instrumentId,
                String.valueOf(trendType), String.valueOf(price),
                String.valueOf(size), priceType, leverage, clientOId),
                new TypeReference<RespTradeV3>() {});
    }

    private RespCancelTradeV3 doCancelOrder(String instrumentId, String orderId) {
        return JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.doCancelTrade(orderId, instrumentId),
                new TypeReference<RespCancelTradeV3>() {});
    }

    private RespBatchCancelTradeV3 doCancelMultiOrder(String instrumentId, List<String> orderIds) {
        return JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.doBatchCancelTrade(instrumentId, JsonUtil.objToJson(orderIds)),
                new TypeReference<RespBatchCancelTradeV3>() {});
    }

    @Override
    public void buyShort(String instrumentId, double price, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mShortLeverage));
    }

    @Override
    public void buyShortDirectly(String instrumentId, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mShortLeverage));
    }

    @Override
    public void sellShort(String instrumentId, double price, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_SELL_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mShortLeverage));
    }

    @Override
    public void sellShortDirectly(String instrumentId, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE, mShortLeverage));
    }

    @Override
    public void buyLong(String instrumentId, double price, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mLongLeverage));
    }

    @Override
    public void buyLongDirectly(String instrumentId, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE, mLongLeverage));
    }

    @Override
    public void sellLong(String instrumentId, double price, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE, mLongLeverage));
    }

    @Override
    public void sellLongDirectly(String instrumentId, double amount) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE, mLongLeverage));
    }

    @Override
    public void cancelOrder(String instrumentId, String orderId) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, orderId));
    }

    @Override
    public void cancelOrders(String instrumentId, List<String> orderIds) {
        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, orderIds));
    }

    public static class FutureTradeInfo extends BaseM {

        // 进行交易记录
        public String instrumentId;
        public double price;
        public double amount;
        public byte trendType;
        public String priceType;
        public String clientOId;
        public int leverage;

        // 取消订单记录
        public String orderId;
        boolean isCancelOp;
        public List<String> orderIds;

        FutureTradeInfo(String instrumentId, double price, double amount, byte trendType, String priceType, int leverage) {
            this.instrumentId = instrumentId;
            this.price = price;
            this.amount = amount;
            this.trendType = trendType;
            this.priceType = priceType;
            this.leverage = leverage;
            this.isCancelOp = false;
        }

        FutureTradeInfo(String instrumentId, String orderId) {
            this.instrumentId = instrumentId;
            this.orderId = orderId;
            this.isCancelOp = true;
        }

        FutureTradeInfo(String instrumentId, List<String> orderIds) {
            this.instrumentId = instrumentId;
            this.orderIds = orderIds;
            this.isCancelOp = true;
        }

    }
}
