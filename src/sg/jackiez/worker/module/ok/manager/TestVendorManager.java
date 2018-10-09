package sg.jackiez.worker.module.ok.manager;

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
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.utils.Config;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.FileUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.PIZ;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class TestVendorManager {

    private static final String TAG = "TestVendorManager";

    public static boolean mIsDebug = true;
    public static final String PATH_PROFIT_LOG = "log";
    private PIZ mPIZ = new PIZ();

    private AccountDataGrabber mAccountDataGrabber;
    private FutureDataGrabber mFutureDataGrabber;

    AccountStateChangeCallback mStateChangeCallback = new AccountStateChangeCallback() {
        @Override
        public void onAccountInfoUpdated() {

        }

        @Override
        public void onAccountInfoOutdated() {

        }
    };

    FutureDataChangeCallback mDataChangeCallback = new FutureDataChangeCallback() {
        @Override
        public void onDepthUpdated(DepthInfo depthInfo) {
            List<KlineInfo> klineInfos = mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN);
            if (klineInfos != null && !klineInfos.isEmpty()) {

            }
        }

        @Override
        public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos, String longTimeType, List<KlineInfo> longKlineInfos) {

        }

        @Override
        public void onTickerDataUpdate(Ticker ticker) {

        }
    };

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

    public void writeLineToFile(String line) {
        long curTime = System.currentTimeMillis();
        String date = DateUtil.formatDate(curTime);
        File f = FileUtil.getFileBaseCurrentWork(PATH_PROFIT_LOG + File.separator + date + ".log");
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
