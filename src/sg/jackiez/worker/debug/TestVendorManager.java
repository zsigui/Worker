package sg.jackiez.worker.debug;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.module.ok.callback.AccountStateChangeCallback;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.FutureDataChangeCallback;
import sg.jackiez.worker.module.ok.callback.VendorResultCallback;
import sg.jackiez.worker.module.ok.handler.AccountDataGrabber;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.handler.FutureVendor;
import sg.jackiez.worker.module.ok.manager.AccountManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.module.util.UniversalDataSource;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.PIZ;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

/**
 * 模拟关注线
 */
public class TestVendorManager {

    private static final String TAG = "TestVendorManager";

    private PIZ mPIZ = new PIZ();

    private AccountDataGrabber mAccountDataGrabber;
    private FutureDataGrabber mFutureDataGrabber;
    private FutureVendor mFutureVendor;
    private TestAccount mTestAccount = new TestAccount();
    private boolean mIsDataChange;


    private long mLastTradeTime;
    private double mLastSignal;
    private String symbol = OKTypeConfig.SYMBOL_EOS;

    private AccountStateChangeCallback mStateChangeCallback = new AccountStateChangeCallback() {
        @Override
        public void onAccountInfoUpdated() {
            if (mIsDataChange) {
                handleKlineForSignal(mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN));
            }
        }

        @Override
        public void onAccountInfoOutdated() {

        }
    };

    private FutureDataChangeCallback mDataChangeCallback = new FutureDataChangeCallback() {
        @Override
        public void onDepthUpdated(DepthInfo depthInfo) {
            KlineInfo klineInfo = new KlineInfo();
            klineInfo.time = System.currentTimeMillis();
            klineInfo.close = (depthInfo.asks.get(depthInfo.asks.size() - 1).get(0) +
                    + depthInfo.bids.get(0).get(0)) / 2;
            List<KlineInfo> klineInfos = new ArrayList<>(mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN));
            klineInfos.add(klineInfo);
            handleKlineForSignal(klineInfos);
        }

        @Override
        public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos, String longTimeType, List<KlineInfo> longKlineInfos) {
            handleKlineForSignal(shortKlineInfos);
        }

        @Override
        public void onTickerDataUpdate(Ticker ticker) {
            // 暂时忽略这个的处理
        }

        @Override
        public void onGetTradeHistory(List<TradeHistoryItem> tradeHistory) {

        }
    };

    private VendorResultCallback mVendorResultCallback = new VendorResultCallback() {
        @Override
        public void onTradeSuccess() {
            printProfitList();
            // 重新拉取账户信息
            AccountManager.get().setNeedUpdateInfo(true);

            double curEosToUsdt;
            if (mFutureDataGrabber.getDepthInfo() != null) {
                DepthInfo depthInfo = mFutureDataGrabber.getDepthInfo();
                curEosToUsdt = (depthInfo.bids.get(0).get(0) + depthInfo.asks.get(depthInfo.asks.size() - 1).get(0));
            } else {
                List<KlineInfo> kinfo = mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN);
                curEosToUsdt = kinfo.get(kinfo.size() - 1).close;
            }
            SLogUtil.i(TAG, "执行交易完成：当前执行Eos金额是：$" + curEosToUsdt + ", signal = " + mLastSignal);
            mLastTradeTime = System.currentTimeMillis();
            handleBuyAndSell(curEosToUsdt, mLastSignal);
        }

        @Override
        public void onTradeFail() {

        }

        @Override
        public void onCancelOrderSuccess() {

        }

        @Override
        public void onCancelOrderFail() {

        }
    };

    private void handleKlineForSignal(List<KlineInfo> klineInfos) {
        if (klineInfos == null || klineInfos.isEmpty()) {
            return;
        }
        if (mTestAccount.getCurrentMoney() < TestAccount.INIT_MONEY
                && (1 - mTestAccount.getCurrentMoney() / TestAccount.INIT_MONEY) > TestAccount.TOTAL_LOSS_RATE_TO_STOP
                && mTestAccount.getHoldUpPage() == 0 && mTestAccount.getHoldDownPage() == 0) {
            // 未持有情况下亏光了
            stop();
            return;
        }
        // 先判断当前是否有持仓
        if (AccountManager.get().isNeedUpdateInfo()) {
            // 再次唤醒
            mAccountDataGrabber.startGrabAccountDataThread();
            mIsDataChange = true;
            return;
        }
        if (mLastSignal == Double.MAX_EXPONENT) {
            return;
        }
        if (System.currentTimeMillis() - mLastTradeTime <= 3_000) {
            return;
        }

        mIsDataChange = false;
        double curEosToUsdt;
        double signal = mPIZ.calculate(klineInfos);
        if (mFutureDataGrabber.getDepthInfo() != null) {
            DepthInfo depthInfo = mFutureDataGrabber.getDepthInfo();
            curEosToUsdt = (depthInfo.bids.get(0).get(0) + depthInfo.asks.get(depthInfo.asks.size() - 1).get(0));
        } else {
            List<KlineInfo> kinfo = mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN);
            curEosToUsdt = kinfo.get(kinfo.size() - 1).close;
        }
        SLogUtil.i(TAG, "当前执行Eos金额是：$" + curEosToUsdt + ", signal = " + signal);
        mLastSignal = signal;
        // 随意执行买卖操作
        if (mTestAccount.isHoldContracts()) {
            // 没有持有合约，则当前根据信号量执行买入卖出
            if (signal >= 1) {
                mFutureVendor.buyLongDirectly(symbol, 10);
            } else if (signal <= -1) {
                mFutureVendor.buyLongDirectly(symbol, 10);
            } else if (signal > 0) {
                mFutureVendor.buyLongDirectly(symbol, 10);
            } else if (signal < 0) {
                mFutureVendor.buyLongDirectly(symbol, 10);
            }
        } else {
            // 持有合约，这个时候判断卖出
            double profitRate;
            if (mTestAccount.getHoldDownPage() > 0) {
                profitRate = 1 - curEosToUsdt / mTestAccount.getHoldDownPageValue() * mTestAccount.getLeverRate();
                if (-profitRate > TestAccount.MAX_LOSS_RATE) {
                    // 赔过额度，卖出
                    mFutureVendor.buyLongDirectly(symbol, 10);
                } else if (signal > 0 || profitRate > 0.5f) {
                    mFutureVendor.buyLongDirectly(symbol, 10);
                }
            }
            if (mTestAccount.getHoldUpPage() > 0) {
                profitRate = 1 - curEosToUsdt / mTestAccount.getHoldUpPageValue() * mTestAccount.getLeverRate();
                if (-profitRate > TestAccount.MAX_LOSS_RATE) {
                    mFutureVendor.buyLongDirectly(symbol, 10);
                } else if (signal < 0 || profitRate > 0.5f) {
                    mFutureVendor.buyLongDirectly(symbol, 10);
                }
            }
        }
    }

    private void handleBuyAndSell(double curEosToUsdt, double signal) {
        mLastSignal = Double.MAX_EXPONENT;
        if (mTestAccount.isHoldContracts()) {
            // 没有持有合约，则当前根据信号量执行买入卖出
            if (signal >= 1) {
                mTestAccount.longBuyAll(curEosToUsdt);
            } else if (signal <= -1) {
                mTestAccount.shortBuyAll(curEosToUsdt);
            } else if (signal > 0) {
                mTestAccount.longBuyHalf(curEosToUsdt);
            } else if (signal < 0) {
                mTestAccount.shortBuyHalf(curEosToUsdt);
            }
        } else {
            // 持有合约，这个时候判断卖出
            double profitRate;
            if (mTestAccount.getHoldDownPage() > 0) {
                profitRate = 1 - curEosToUsdt / mTestAccount.getHoldDownPageValue() * mTestAccount.getLeverRate();
                if (-profitRate > TestAccount.MAX_LOSS_RATE) {
                    // 赔过额度，卖出
                    mTestAccount.shortSell(curEosToUsdt);
                } else if (signal > 0 || profitRate > 0.5f) {
                    mTestAccount.shortSell(curEosToUsdt);
                }
            }
            if (mTestAccount.getHoldUpPage() > 0) {
                profitRate = 1 - curEosToUsdt / mTestAccount.getHoldUpPageValue() * mTestAccount.getLeverRate();
                if (-profitRate > TestAccount.MAX_LOSS_RATE) {
                    mTestAccount.longSell(curEosToUsdt);
                } else if (signal < 0 || profitRate > 0.5f) {
                    mTestAccount.longSell(curEosToUsdt);
                }
            }
        }
    }

    public void start() {
        OkConfig.IS_TEST = true;
        SLogUtil.setPrintFile(true);
        IFutureRestApi futureRestApi = new FutureRestApiV1();
        String contractType = OKTypeConfig.CONTRACT_TYPE_QUARTER;
        mAccountDataGrabber = new AccountDataGrabber(symbol, contractType,
                futureRestApi);
        mFutureDataGrabber = new FutureDataGrabber(symbol, contractType, futureRestApi);
        mAccountDataGrabber.startGrabAccountDataThread();
        mFutureDataGrabber.startAll();
        mFutureVendor = new FutureVendor(futureRestApi, contractType, OKTypeConfig.LEVER_RATE_20);
        mFutureVendor.startTradeThread();
        CallbackManager.get().addAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().addFutureDataChangeCallback(mDataChangeCallback);
        CallbackManager.get().addVendorResultCallback(mVendorResultCallback);
    }

    public void stop() {
        OkConfig.IS_TEST = false;
        mAccountDataGrabber.stopGrabAccountDataThread();
        mFutureDataGrabber.stopAll();
        mFutureVendor.stopTradeThread();
        CallbackManager.get().removeAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().removeFutureDataChangeCallback(mDataChangeCallback);
        CallbackManager.get().removeVendorResultCallback(mVendorResultCallback);

        printProfitList();
    }

    private void printProfitList() {
        StringBuilder builder = new StringBuilder();
        builder.append("所有交易结果：\n");
        builder.append(mTestAccount.mProfitRateList);
        builder.append('\n');
        builder.append("总交易次数：").append(mTestAccount.mProfitRateList.size()).append('\n');
        builder.append("最终盈利率：").append(TestAccount.INIT_MONEY / mTestAccount.getCurrentMoney() * 100).append("%\n");
        builder.append("原先金额：¥").append(TestAccount.INIT_MONEY * UniversalDataSource.get().getUsdToCny()).append('\n');
        builder.append("最终金额：¥").append(mTestAccount.getCurrentMoney() * UniversalDataSource.get().getUsdToCny()).append('\n');
        SLogUtil.i(TAG, builder.toString());
    }
}
