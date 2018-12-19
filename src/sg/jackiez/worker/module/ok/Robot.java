package sg.jackiez.worker.module.ok;

import java.util.List;

import sg.jackiez.worker.module.ok.callback.AccountStateChangeCallback;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.FutureDataChangeCallback;
import sg.jackiez.worker.module.ok.callback.VendorResultCallback;
import sg.jackiez.worker.module.ok.handler.AccountDataGrabber;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.handler.vendor.VendorDataHandler;
import sg.jackiez.worker.module.ok.manager.PrecursorManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.CustomSharp;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class Robot {

    public static final String TAG = "RobotTrade";

    private AccountDataGrabber mAccountDataGrabber;
    private FutureDataGrabber mFutureDataGrabber;
    private VendorDataHandler mFutureVendor;
    private CustomSharp mSharp = new CustomSharp();

//    private IPerformance mPerformance;
    private boolean mIsDataChange;


    private static final int PAY_GAP_TIME = 20_000; // 间隔20s内不买卖
    private double mLastPayMoney = 0;
    private double mMiddleEndPointMoney = 0;    // 记录可能的最大亏损
    private float mMiddleMaxProfitRate = 0f;    // 记录中间最大盈利率
    private long mLastPayRecordTime = 0;
    private int mLastPayDirection = CustomSharp.DIRECTION_AVG;


    // 用于统计一段时间结果
    private float mAccumulatedBigLossRateCount = 0; // 累积统计次数
    private int mAccumulatedCount = 0; // 累积统计次数
    private float mAccumulateProfitAndLossRate = 0; // 累积盈亏率

    static class PayRate {
        float profitAndLossRate; // 盈亏率
        double clearPrice; // 清仓价格
        long clearTime; // 清仓时间戳
        double orderPrice; // 开仓价格
        int orderDirection; // 开仓方向
        long orderTime; // 开仓时间戳
        float maxProfitRate; // 中间最大盈利率
    }

    private AccountStateChangeCallback mStateChangeCallback = new AccountStateChangeCallback() {
        @Override
        public void onAccountInfoUpdated() {

        }

        @Override
        public void onAccountInfoOutdated() {

        }
    };

    private FutureDataChangeCallback mDataChangeCallback = new FutureDataChangeCallback() {
        @Override
        public void onDepthUpdated(DepthInfo depthInfo) {

        }

        @Override
        public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos, String longTimeType, List<KlineInfo> longKlineInfos) {

        }

        @Override
        public void onGetUpdatedKlineInfo(String timeType, List<KlineInfo> updated) {

        }

        @Override
        public void onTickerDataUpdate(Ticker ticker) {
            // 暂时忽略这个的处理
        }

        @Override
        public void onGetTradeHistory(List<TradeHistoryItem> tradeHistory) {
            if (tradeHistory == null || tradeHistory.isEmpty()) {
                return;
            }

            final TradeHistoryItem newestTrade = tradeHistory.get(tradeHistory.size() - 1);
            final double curPrice = newestTrade.price;
//            mPerformance.handleBar(newestTrade);

            if (mLastPayDirection == CustomSharp.DIRECTION_UP
                    && curPrice < mMiddleEndPointMoney) {
                // 上一轮看涨，中间出现最高跌价
                mMiddleEndPointMoney = curPrice;
            } else if (mLastPayDirection == CustomSharp.DIRECTION_DOWN
                    && curPrice > mMiddleEndPointMoney) {
                // 上一轮看跌，中间出现最高涨价
                mMiddleEndPointMoney = curPrice;
            }

            final long curTime = System.currentTimeMillis();

            float profitAndLossRate = calculateProfitAndLossRate(mLastPayDirection, mLastPayMoney, curPrice);
            if (profitAndLossRate > mMiddleMaxProfitRate) {
                mMiddleMaxProfitRate = profitAndLossRate;
            }

            if (profitAndLossRate < -30) {
                // 亏损超过30%了，这个时候就当卖出处理。
                buildPayRate(curPrice, curTime, profitAndLossRate);
                clearOrderRecord(curTime);
            } else if (profitAndLossRate > 100) {
                // 盈利超过100%了，卖出
                buildPayRate(curPrice, curTime, profitAndLossRate);
                clearOrderRecord(curTime);
            }


            final int direction = mSharp.judgeEosDirection(
                    mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN),
                    curPrice);

            SLogUtil.d(TAG, "onGetTradeHistory() 当前金额：$" + curPrice + "，判断下单方向：" + getDirectionStr(direction));

            if (curTime - mLastPayRecordTime < PAY_GAP_TIME) {
                // 间隔时间内，不做处理

                if (mLastPayDirection != CustomSharp.DIRECTION_AVG && direction != mLastPayDirection) {
                    // 当前有价格变动的需求
                    SLogUtil.i(TAG, String.format("限制时间内发生与上次判断可下单方向相反，当前盈亏率：%.2f%%",
                            calculateProfitAndLossRate(mLastPayDirection, mLastPayMoney, curPrice)));
                }

                return;
            }


            if (mLastPayDirection == CustomSharp.DIRECTION_AVG) {
                if (direction == CustomSharp.DIRECTION_DOWN
                        || direction == CustomSharp.DIRECTION_UP) {
                    // 平均数，这个时候执行下单
                    mLastPayDirection = direction;
                    mLastPayMoney = curPrice;
                    mLastPayRecordTime = curTime;
                    SLogUtil.i(TAG, "当前下单金额：" + mLastPayMoney + ", 下单方向：" + getDirectionStr(mLastPayDirection));
                }
            } else {
                if (mLastPayDirection != direction && mMiddleMaxProfitRate > 30
                        && profitAndLossRate * 3 > mMiddleMaxProfitRate) {
                    // 出现了方向上的回撤，且在盈利30%以上的情况下，回撤超过了33%，提前卖出
                    buildPayRate(curPrice, curTime, profitAndLossRate);
                    clearOrderRecord(curTime);
                } else {
                    // 正常判断周期内，打印中间值
                    printMiddleState(curPrice, curTime, direction);
                }
            }
        }
    };

    private void buildPayRate(double curPrice, long curTime, float profitAndLossRate) {
        PayRate payRate = new PayRate();
        payRate.clearPrice = curPrice;
        payRate.profitAndLossRate = profitAndLossRate;
        payRate.orderDirection = mLastPayDirection;
        payRate.orderPrice = mLastPayMoney;
        payRate.clearTime = curTime;
        payRate.orderTime = mLastPayRecordTime;
        payRate.maxProfitRate = mMiddleMaxProfitRate;
        printPayRate(payRate);

        mAccumulatedCount++;
        mAccumulateProfitAndLossRate += profitAndLossRate;
        SLogUtil.i(TAG, "");
        SLogUtil.i(TAG, "======*******************************=======");
        SLogUtil.i(TAG, "已执行次数：" + mAccumulatedCount + ", 总盈亏率：" + mAccumulateProfitAndLossRate + "%");
        SLogUtil.i(TAG, "======*******************************=======");
        SLogUtil.i(TAG, "");
        if (mAccumulateProfitAndLossRate <= -60) {
            // 总亏损达到了60%
            mAccumulatedCount = 0;
            mAccumulateProfitAndLossRate = 0;
            mAccumulatedBigLossRateCount++;
            SLogUtil.e(TAG, "");
            SLogUtil.e(TAG, "总亏损已达: " + mAccumulateProfitAndLossRate
                    + ", 目前第" + mAccumulatedBigLossRateCount + "次");
            SLogUtil.e(TAG, "");
        }
    }

    private void printPayRate(PayRate payRate) {
        SLogUtil.i(TAG, "");
        SLogUtil.i(TAG, "============================================");
        SLogUtil.i(TAG, "此轮执行" + getDirectionStr(payRate.orderDirection));
        SLogUtil.i(TAG, "下单价格：$" + payRate.orderPrice + "; 清单价格：$" + payRate.clearPrice
                + "; 盈亏比率：" + payRate.profitAndLossRate + "%; 中间最大盈利率：" + payRate.maxProfitRate + "%");
        SLogUtil.i(TAG, "下单时间：" + DateUtil.formatISOTime(payRate.orderTime)
                + "; 清单时间：" + DateUtil.formatISOTime(payRate.clearTime));
        SLogUtil.i(TAG, "============================================");
        SLogUtil.i(TAG, "");
    }

    private void printMiddleState(double curPrice, long curTime, int direction) {
        SLogUtil.i(TAG, "");
        SLogUtil.i(TAG, "============================================");
        SLogUtil.i(TAG, "当前" + getDirectionStr(direction));
        SLogUtil.i(TAG, "触发价格：$" + curPrice);
        SLogUtil.i(TAG, "上轮价格：$" + mLastPayMoney + ", 方向：" + getDirectionStr(mLastPayDirection)
                + ", 距离现在时间：" + (curTime - mLastPayRecordTime) / 1000 + "s");
        SLogUtil.i(TAG, String.format("相比上轮盈亏率：%.2f%%，中间最大盈亏率：%.2f%%",
                calculateProfitAndLossRate(mLastPayDirection, mLastPayMoney, curPrice),
                calculateProfitAndLossRate(mLastPayDirection, mLastPayMoney, mMiddleEndPointMoney)));
        SLogUtil.i(TAG, "============================================");
        SLogUtil.i(TAG, "");
    }

    private void clearOrderRecord(long curTime) {
        mLastPayDirection = CustomSharp.DIRECTION_AVG;
        mLastPayMoney = 0;
        mMiddleEndPointMoney = 0;
        mMiddleMaxProfitRate = 0;
        // 为了保证清除后一定时间内不再触发下单逻辑
        mLastPayRecordTime = curTime;
    }

    private String getDirectionStr(int direction) {
        if (direction == CustomSharp.DIRECTION_UP) {
            return "看涨";
        } else if (direction == CustomSharp.DIRECTION_DOWN) {
            return "看跌";
        } else {
            return "不定";
        }
    }

    private float calculateProfitAndLossRate(int orderDirection, double orderPrice,
                                             double curPrice) {
        if (orderDirection == CustomSharp.DIRECTION_UP) {
            // 上涨阶段，所以只有当 当前价 < 下单价，才算是亏损
            return (float) ((curPrice / orderPrice - 1) * 100 * 20);
        } else if (orderDirection == CustomSharp.DIRECTION_DOWN) {
            // 下跌阶段，所以只有当 当前价 > 下单价，才算是亏损
            return (float) ((1 - curPrice / orderPrice) * 100 * 20);
        }
        return 0f;
    }

    private VendorResultCallback mVendorResultCallback = new VendorResultCallback() {
        @Override
        public void onTradeSuccess(String clientOId, String orderId, String instrumentId) {
//            mPerformance.afterTrade();
        }

        @Override
        public void onTradeFail(int errCode, String errMsg) {

        }

        @Override
        public void onCancelOrderSuccess(String orderId, String instrumentId) {

        }

        @Override
        public void onCancelOrderFail(int errCode, String errMsg) {

        }
    };


    public void start() {
        SLogUtil.setPrintFile(true);
        SLogUtil.setDebugLevel(SLogUtil.Level.DEBUG);
        PrecursorManager precursorManager = PrecursorManager.get();
        precursorManager.init(OKTypeConfig.SYMBOL_EOS, OKTypeConfig.CONTRACT_TYPE_QUARTER);
//        mPerformance.init();
        mFutureDataGrabber = new FutureDataGrabber(precursorManager.getInstrumentId());
        mAccountDataGrabber = new AccountDataGrabber();
        mFutureVendor = new VendorDataHandler(precursorManager.getLongLeverage(),
                precursorManager.getShortLeverage());
//        mAccountDataGrabber.startGrabAccountDataThread();
        mFutureDataGrabber.startKlineGrabThread();
        mFutureDataGrabber.startTradeGrabThread();
//        mFutureVendor.startTradeThread();
        CallbackManager.get().addAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().addFutureDataChangeCallback(mDataChangeCallback);
        CallbackManager.get().addVendorResultCallback(mVendorResultCallback);
    }

    public void stop() {
        mAccountDataGrabber.stopGrabAccountDataThread();
        mFutureDataGrabber.stopAll();
        mFutureVendor.stopTradeThread();
        CallbackManager.get().removeAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().removeFutureDataChangeCallback(mDataChangeCallback);
        CallbackManager.get().removeVendorResultCallback(mVendorResultCallback);
    }
}
