package sg.jackiez.worker.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.AccountStateChangeCallback;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.FutureDataChangeCallback;
import sg.jackiez.worker.module.ok.handler.AccountDataGrabber;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.manager.AccountManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.module.util.UniversalDataSource;
import sg.jackiez.worker.utils.Config;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.FileUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.PIZ;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class TestVendorManager {

    private static final String TAG = "TestVendorManager";

    private static final String PATH_PROFIT_LOG = "log";
    private PIZ mPIZ = new PIZ();

    private AccountDataGrabber mAccountDataGrabber;
    private FutureDataGrabber mFutureDataGrabber;
    private TestAccount mTestAccount = new TestAccount();
    private boolean mIsDataChange;

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
            mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN).add(klineInfo);
            handleKlineForSignal(mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN));
        }

        @Override
        public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos, String longTimeType, List<KlineInfo> longKlineInfos) {
            handleKlineForSignal(shortKlineInfos);
        }

        @Override
        public void onTickerDataUpdate(Ticker ticker) {
            // 暂时忽略这个的处理
        }
    };

    private void handleKlineForSignal(List<KlineInfo> klineInfos) {
        if (klineInfos == null || klineInfos.isEmpty()) {
            return;
        }
        if (mTestAccount.getCurrentMoney() < TestAccount.INIT_MONEY
                && (1 - mTestAccount.getCurrentMoney() / TestAccount.INIT_MONEY) > TestAccount.TOTAL_LOSS_RATE_TO_STOP) {
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
        double signal = mPIZ.calculate(klineInfos);
        if (AccountManager.get().getEosContract().contracts.isEmpty()) {
            // 没有持有合约，则当前根据信号量执行买入卖出
        } else {
            // 持有合约，这个时候判断卖出

        }
    }

    public void start() {
        IFutureRestApi futureRestApi = new FutureRestApiV1();
        String symbol = "eos_usdt";
        String contractType = OKTypeConfig.CONTRACT_TYPE_QUARTER;
        mAccountDataGrabber = new AccountDataGrabber(symbol, contractType,
                futureRestApi);
        mFutureDataGrabber = new FutureDataGrabber(symbol, contractType, futureRestApi);
        mAccountDataGrabber.startGrabAccountDataThread();
        mFutureDataGrabber.startAll();
        CallbackManager.get().addAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().addFutureDataChangeCallback(mDataChangeCallback);
    }

    public void stop() {
        mAccountDataGrabber.stopGrabAccountDataThread();
        mFutureDataGrabber.stopAll();
        CallbackManager.get().removeAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().removeFutureDataChangeCallback(mDataChangeCallback);

        StringBuilder builder = new StringBuilder();
        builder.append("所有交易结果：\n");
        builder.append(mTestAccount.mProfitRateList);
        builder.append('\n');
        builder.append("总交易次数：").append(mTestAccount.mProfitRateList.size()).append('\n');
        builder.append("最终盈利率：").append(TestAccount.INIT_MONEY / mTestAccount.getCurrentMoney() * 100).append("%\n");
        builder.append("原先金额：¥").append(TestAccount.INIT_MONEY * UniversalDataSource.get().getUsdToCny()).append('\n');
        builder.append("最终金额：¥").append(mTestAccount.getCurrentMoney() * UniversalDataSource.get().getUsdToCny()).append('\n');
        writeLineToFile(builder.toString());
    }

    public void writeLineToFile(String line) {
        long curTime = System.currentTimeMillis();
        String date = DateUtil.formatDate(curTime);
        File f = FileUtil.getFileBaseCurrentWork(PATH_PROFIT_LOG + File.separator + date + ".log");
        if (f == null) {
            SLogUtil.v(TAG, "no file path is found.");
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f, true);
            fos.write(line.getBytes(Config.DEFAULT_SYS_CHARSET));
            fos.write('\n');
            fos.flush();
        } catch (IOException e) {
            SLogUtil.v(TAG, e);
        } finally {
            FileUtil.closeIO(fos);
        }
    }
}
