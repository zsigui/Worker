//package sg.jackiez.worker.module.ok.handler.vendor;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//import sg.jackiez.worker.module.ok.OKTypeConfig;
//import sg.jackiez.worker.module.ok.OkConfig;
//import sg.jackiez.worker.module.ok.callback.CallbackManager;
//import sg.jackiez.worker.module.ok.model.base.BaseM;
//import sg.jackiez.worker.module.ok.model.resp.RespCancelTrade;
//import sg.jackiez.worker.module.ok.model.resp.RespTrade;
//import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
//import sg.jackiez.worker.module.ok.utils.JsonUtil;
//import sg.jackiez.worker.utils.SLogUtil;
//import sg.jackiez.worker.utils.common.CommonUtil;
//import sg.jackiez.worker.utils.thread.DefaultThread;
//
///**
// * 用于进行期货交易及现有资产管理的处理类
// */
//public class FutureVendor implements IVendor{
//
//    private static final String TAG = "FutureVendor";
//
//    private static final int MAXT_RETRY_TIME = 3;
//
//    private IFutureRestApi mRestApi;
//    private String mCurContractType;
//    private String mLeverRate;
//
//    private final Object mLockObj = new Object();
//    private Thread mTradeThread;
//    private boolean mIsTradeThreadRunning;
//
//    private ConcurrentLinkedQueue<FutureTradeInfo> mTradeInfoList = new ConcurrentLinkedQueue<>();
//
//    public FutureVendor(IFutureRestApi restApi) {
//        this(restApi, OKTypeConfig.CONTRACT_TYPE_QUARTER, OKTypeConfig.LEVER_RATE_20);
//    }
//
//    public FutureVendor(IFutureRestApi restApi, String curContractType, String leverRate) {
//        mRestApi = restApi;
//        mCurContractType = curContractType;
//        mLeverRate = leverRate;
//    }
//
//    private void addTradeInfoAndNotify(FutureTradeInfo info) {
//        if (info == null) {
//            return;
//        }
//        mTradeInfoList.add(info);
//        startTradeThread();
//    }
//
//    private boolean isTradeThreadAlive() {
//        return mTradeThread != null && mTradeThread.isAlive()
//                && !mTradeThread.isInterrupted();
//    }
//
//    public void startTradeThread() {
//        if (mTradeThread != null && mTradeThread.isAlive()
//                && !mTradeThread.isInterrupted()) {
//            // 已经在跑,直接唤醒即可
//            synchronized (mLockObj) {
//                mLockObj.notify();
//            }
//            return;
//        }
//        mIsTradeThreadRunning = true;
//        mTradeThread = new DefaultThread(() -> {
//
//            FutureTradeInfo info;
//            long startTime;
//            while (mIsTradeThreadRunning) {
//
//                info = mTradeInfoList.poll();
//                if (info != null) {
//                    startTime = System.currentTimeMillis();
//                    if (info.isCancelOp) {
//                        handleCancelOrder(info);
//                    } else {
//                        handleTrade(info);
//                    }
//                    SLogUtil.i(TAG, "交易请求完成，花费总时间: " + (System.currentTimeMillis() - startTime) + "ms");
//                }
//
//                if (mTradeInfoList.isEmpty()) {
//                    synchronized (mLockObj) {
//                        try {
//                            mLockObj.wait();
//                        } catch (InterruptedException e) {
//                            SLogUtil.v(e);
//                        }
//                    }
//                }
//            }
//            if (!mTradeInfoList.isEmpty()) {
//                SLogUtil.i(TAG, "交易线程暂停，当前尚有未执行操作：" + mTradeInfoList);
//            }
//        });
//        mTradeThread.setPriority(Thread.MAX_PRIORITY);
//        mTradeThread.start();
//    }
//
//    public void stopTradeThread() {
//        if (isTradeThreadAlive()) {
//            mIsTradeThreadRunning = false;
//            synchronized (mLockObj) {
//                mLockObj.notify();
//            }
//            mTradeThread.interrupt();
//        }
//    }
//
//    private void handleCancelOrder(FutureTradeInfo info) {
//        int retryTime;
//        RespCancelTrade rsp = null;
//        retryTime = MAXT_RETRY_TIME;
//        while (rsp == null && retryTime-- > 0) {
//            rsp = doCancelOrder(info.symbol, info.orderId);
//        }
//        if (OkConfig.IS_TEST) {
//            // 测试，直接当成成功处理
//            CallbackManager.get().onCancelOrderSuccess();
//        } else {
//            // TODO 简单处理，后续需要重新调整
//            if (rsp == null) {
//                CallbackManager.get().onCancelOrderFail();
//            } else {
//                if (CommonUtil.isEmpty(rsp.order_id)) {
//                    // 单笔订单处理
//                    if (rsp.result) {
//                        // 成功
//                        CallbackManager.get().onCancelOrderSuccess();
//                    } else {
//                        CallbackManager.get().onCancelOrderFail();
//                    }
//                } else {
//                    // 多笔订单处理
//                    if (CommonUtil.isEmpty(rsp.success)) {
//                        CallbackManager.get().onCancelOrderFail();
//                    } else {
//                        CallbackManager.get().onCancelOrderSuccess();
//                    }
//                }
//            }
//        }
//    }
//
//    private void handleTrade(FutureTradeInfo info) {
//        int retryTime;
//        RespTrade rsp = null;
//        retryTime = MAXT_RETRY_TIME;
//        while (rsp == null && retryTime-- > 0) {
//            rsp = doTrade(info.symbol, info.price, info.amount, info.trendType,
//                    info.priceType);
//        }
//        if (OkConfig.IS_TEST) {
//            // 测试，直接当成成功处理
//            CallbackManager.get().onTradeSuccess();
//        } else {
//            // TODO 简单处理，后续需要重新调整
//            if (rsp == null || !rsp.result) {
//                CallbackManager.get().onTradeFail();
//            } else {
//                CallbackManager.get().onTradeSuccess();
//            }
//        }
//    }
//
//    private RespTrade doTrade(String symbol, double price, double amount,
//                         byte trendType, String priceType) {
//        return JsonUtil.jsonToSuccessDataForFuture(mRestApi.futureTrade(symbol,
//                mCurContractType, price == 0 ? null : String.valueOf(price),
//                String.valueOf(amount), String.valueOf(trendType), priceType, mLeverRate),
//                new TypeReference<RespTrade>() {});
//    }
//
//    private RespCancelTrade doCancelOrder(String symbol, String orderId) {
//        return JsonUtil.jsonToSuccessDataForFuture(mRestApi.futureCancelOrder(symbol, mCurContractType, orderId),
//                new TypeReference<RespCancelTrade>() {});
//    }
//
//    @Override
//    public void buyShort(String instrumentId, double price, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
//                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE));
//    }
//
//    @Override
//    public void buyShortDirectly(String instrumentId, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
//                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE));
//    }
//
//    @Override
//    public void sellShort(String instrumentId, double price, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_SELL_SHORT,
//                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE));
//    }
//
//    @Override
//    public void sellShortDirectly(String instrumentId, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
//                OKTypeConfig.PRICE_TYPE_MARKET_PRICE));
//    }
//
//    @Override
//    public void buyLong(String instrumentId, double price, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
//                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE));
//    }
//
//    @Override
//    public void buyLongDirectly(String instrumentId, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
//                OKTypeConfig.PRICE_TYPE_MARKET_PRICE));
//    }
//
//    @Override
//    public void sellLong(String instrumentId, double price, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, price, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
//                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE));
//    }
//
//    @Override
//    public void sellLongDirectly(String instrumentId, double amount) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
//                OKTypeConfig.PRICE_TYPE_MARKET_PRICE));
//    }
//
//    @Override
//    public void cancelOrder(String instrumentId, String orderId) {
//        addTradeInfoAndNotify(new FutureTradeInfo(instrumentId, orderId));
//    }
//
//    @Override
//    public void cancelOrders(String instrumentId, List<String> orderIds) {
//        StringBuilder builder = new StringBuilder();
//        for (String orderId : orderIds) {
//            builder.append(orderId).append(",");
//        }
//        builder.deleteCharAt(builder.length() - 1);
//        cancelOrder(instrumentId, builder.toString());
//    }
//
//    static class FutureTradeInfo extends BaseM {
//
//        // 进行交易记录
//        String symbol;
//        double price;
//        double amount;
//        byte trendType;
//        String priceType;
//
//        // 取消订单记录
//        String orderId;
//        boolean isCancelOp;
//
//        FutureTradeInfo(String symbol, double price, double amount, byte trendType, String priceType) {
//            this.symbol = symbol;
//            this.price = price;
//            this.amount = amount;
//            this.trendType = trendType;
//            this.priceType = priceType;
//            this.isCancelOp = false;
//        }
//
//        FutureTradeInfo(String symbol, String orderId) {
//            this.symbol = symbol;
//            this.orderId = orderId;
//            this.isCancelOp = true;
//        }
//
//    }
//}
