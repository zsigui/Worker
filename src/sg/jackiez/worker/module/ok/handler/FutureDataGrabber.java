package sg.jackiez.worker.module.ok.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.FutureDataChangedCallback;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.resp.RespTicker;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.module.ok.utils.CompareUtil;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class FutureDataGrabber {

    private static final String TAG = "FutureDataGrabber";

    private IFutureRestApi mRestApi = new FutureRestApiV1();
    private FutureVendor mVendor;
    private String mSymbol;
    private String mContractType;

    private List<Ticker> mTickers = new ArrayList<>();
    private HashMap<String, List<KlineInfo>> mStoreKlinesMap = new HashMap<>(2);

    private final int KLINE_GAP_TIME = 800;
    private final int TICKER_GAP_TIME = 150;

    private boolean mIsRunning = false;
    private Thread mTickerGrabThread;
    private Thread mKlineGrabThread;

    private FutureDataChangedCallback mDataChangedCallback;

    public FutureDataGrabber() {
        mVendor = new FutureVendor(mRestApi, mContractType, OKTypeConfig.LEVER_RATE_20);
    }

    private void sleepIfInTime(String prex, long lastTime, int gapTime) {
        long spendTime = System.currentTimeMillis() - lastTime;
        SLogUtil.v(TAG, prex + ": total spend time : " + spendTime + " ms");
        if (spendTime < gapTime) {
            try {
                Thread.sleep(gapTime - spendTime);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void startTickerGrabThread() {
        if (mTickerGrabThread != null && !mTickerGrabThread.isInterrupted()) {
            // 先中断之前的
            mTickerGrabThread.interrupt();
        }
        mTickerGrabThread = new DefaultThread(() -> {
            RespTicker tickerNew, tickerOld = null;
            long lastTime;
            while (mIsRunning) {
                lastTime = System.currentTimeMillis();
                try {
                    tickerNew = JsonUtil.jsonToSuccessDataForFuture(mRestApi.futureTicker(mSymbol, mContractType),
                            RespTicker.class);
                    if (tickerNew != null && tickerNew.ticker != null) {
                        SLogUtil.v(TAG, "startTickerGrabThread() 当前获取不到正确行情数据!");
                        if (tickerOld == null || !CompareUtil.equal(tickerNew.ticker, tickerOld.ticker)) {
                            mDataChangedCallback.onTickerDataUpdate(tickerNew.ticker);
                            tickerOld = tickerNew;
                        }
                    }
                } catch (Throwable e) {
                    SLogUtil.v(TAG, "startTickerGrabThread() 读取过程出现异常!");
                } finally {
                    sleepIfInTime("ticker grab", lastTime, TICKER_GAP_TIME);
                }
            }
        });
        mTickerGrabThread.setPriority(Thread.NORM_PRIORITY);
        mTickerGrabThread.start();
    }

    private void startKlineGrabThread() {
        if (mKlineGrabThread != null && !mKlineGrabThread.isInterrupted()) {
            mKlineGrabThread.interrupt();
        }
        mKlineGrabThread = new DefaultThread(() -> {
            List<KlineInfo> _1minKlines, _15minKlines;
            long lastTime;
            boolean isUpdate1min, isUpdate15min;
            while (mIsRunning) {
                isUpdate1min = false;
                isUpdate15min = false;
                lastTime = System.currentTimeMillis();
                _1minKlines = JsonUtil.jsonToKlineList(mRestApi.futureKLine(mSymbol, mContractType,
                        OKTypeConfig.KLINE_TYPE_1_MIN, "1000", null));
                if (_1minKlines != null) {
                    mStoreKlinesMap.putIfAbsent(OKTypeConfig.KLINE_TYPE_1_MIN, _1minKlines);
                    if (CompareUtil.equal(_1minKlines.get(0),
                            mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_1_MIN))) {
                        mStoreKlinesMap.put(OKTypeConfig.KLINE_TYPE_1_MIN, _1minKlines);
                        isUpdate1min = true;
                    }
                }

                _15minKlines = JsonUtil.jsonToKlineList(mRestApi.futureKLine(mSymbol, mContractType,
                        OKTypeConfig.KLINE_TYPE_15_MIN, "1000", null));
                if (_15minKlines != null) {
                    mStoreKlinesMap.putIfAbsent(OKTypeConfig.KLINE_TYPE_15_MIN, _15minKlines);
                    if (CompareUtil.equal(_15minKlines.get(0),
                            mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_15_MIN))) {
                        mStoreKlinesMap.put(OKTypeConfig.KLINE_TYPE_15_MIN, _15minKlines);
                        isUpdate15min = true;
                    }
                }

                if (mDataChangedCallback != null
                        && (isUpdate1min || isUpdate15min)) {
                    // 短周期或者长周期的K线更新后，需要进行回调
                    mDataChangedCallback.onKlineInfoUpdated(OKTypeConfig.KLINE_TYPE_1_MIN,
                            mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_1_MIN),
                            OKTypeConfig.KLINE_TYPE_15_MIN,
                            mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_15_MIN));
                }

                sleepIfInTime("1min kline and 15min kline", lastTime, KLINE_GAP_TIME);

            }
        });
        mKlineGrabThread.setPriority(Thread.NORM_PRIORITY);
        mKlineGrabThread.start();
    }

    private void interruptTickerGrabThread() {
        if (mTickerGrabThread != null && !mTickerGrabThread.isInterrupted()) {
            mTickerGrabThread.interrupt();
        }
    }

    private void interruptKlineGrabThread() {
        if (mKlineGrabThread != null && !mKlineGrabThread.isInterrupted()) {
            mKlineGrabThread.interrupt();
        }
    }

    public void start() {
        startKlineGrabThread();
        startTickerGrabThread();
    }

    public void stop() {
        mIsRunning = false;
        interruptTickerGrabThread();
        interruptKlineGrabThread();
    }

}
