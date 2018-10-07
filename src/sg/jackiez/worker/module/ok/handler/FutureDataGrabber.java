package sg.jackiez.worker.module.ok.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
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

	private final int KLINE_GAP_TIME = 500;
	private final int TICKER_GAP_TIME = 500;
	private final int DEPTH_GAP_TIME = 500;

	private boolean mIsTickerGrabRunning = false;
	private boolean mIsKlineGrabRunning = false;
	private boolean mIsDepthGrabRunning = false;
	private Thread mTickerGrabThread;
	private Thread mKlineGrabThread;
	private Thread mDepthGrabThread;

	public FutureDataGrabber() {
		this("eos_usdt", OKTypeConfig.CONTRACT_TYPE_QUARTER);
	}

	public FutureDataGrabber(String symbol, String contractType) {
		mSymbol = symbol;
		mContractType = contractType;
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

	public void startDepthGrabThread() {
		if (mDepthGrabThread != null && mDepthGrabThread.isAlive()
				&& !mDepthGrabThread.isInterrupted()) {
			return;
		}
		mIsDepthGrabRunning = true;
		mDepthGrabThread = new DefaultThread(() -> {
			DepthInfo depthInfoNew, depthInfoOld = null;
			long tickTime;
			long lastTime = System.currentTimeMillis();
			long nowTime;
			while (mIsDepthGrabRunning) {
				tickTime = System.currentTimeMillis();
				try {
					depthInfoNew = JsonUtil.jsonToSuccessDataForFuture(mRestApi.futureDepth(mSymbol, mContractType),
							new TypeReference<DepthInfo>() {
							});
					if (depthInfoNew != null && depthInfoNew.asks != null && depthInfoNew.bids != null
							&& (depthInfoOld == null || depthInfoNew.asks.size() != depthInfoOld.asks.size()
							|| depthInfoNew.bids.size() != depthInfoOld.bids.size()
							|| !CompareUtil.equal(depthInfoNew.asks.get(depthInfoNew.asks.size() - 1).get(0),
							depthInfoOld.asks.get(depthInfoOld.asks.size() - 1).get(0))
							|| !CompareUtil.equal(depthInfoNew.bids.get(0).get(0),
							depthInfoOld.bids.get(0).get(0)))) {
						nowTime = System.currentTimeMillis();
						CallbackManager.get().onDepthUpdated(depthInfoNew);
						SLogUtil.v(TAG, "startTickerGrabThread() 获取到新行情数据, 距上次时间: "
								+ (nowTime - lastTime) + " ms");
						depthInfoOld = depthInfoNew;
						lastTime = nowTime;
					}
				} catch (Throwable e) {
					SLogUtil.v(TAG, "startDepthGrabThread() 读取过程出现异常!");
				} finally {
					sleepIfInTime("depth grab", tickTime, DEPTH_GAP_TIME);
				}
			}
		});
		mDepthGrabThread.setPriority(Thread.NORM_PRIORITY);
		mDepthGrabThread.start();
	}

	public void startTickerGrabThread() {
		if (mTickerGrabThread != null && mTickerGrabThread.isAlive()
				&& !mTickerGrabThread.isInterrupted()) {
			return;
		}
		mIsTickerGrabRunning = true;
		mTickerGrabThread = new DefaultThread(() -> {
			RespTicker tickerNew, tickerOld = null;
			long lastTime = System.currentTimeMillis();
			long nowTime;
			long tickTime;
			while (mIsTickerGrabRunning) {
				tickTime = System.currentTimeMillis();
				try {
					tickerNew = JsonUtil.jsonToSuccessDataForFuture(mRestApi.futureTicker(mSymbol, mContractType),
							new TypeReference<RespTicker>() {
							});
					if (tickerNew != null && tickerNew.ticker != null) {
						if (tickerOld == null || !CompareUtil.equal(tickerNew.ticker, tickerOld.ticker)) {
							CallbackManager.get().onTickerDataUpdate(tickerNew.ticker);
							nowTime = System.currentTimeMillis();
							SLogUtil.v(TAG, "startTickerGrabThread() 获取到新行情数据, 距上次时间: "
									+ (nowTime - lastTime) + " ms");
							lastTime = nowTime;
							tickerOld = tickerNew;
						}
					}
				} catch (Throwable e) {
					SLogUtil.v(TAG, "startTickerGrabThread() 读取过程出现异常!");
				} finally {
					sleepIfInTime("ticker grab", tickTime, TICKER_GAP_TIME);
				}
			}
		});
		mTickerGrabThread.setPriority(Thread.NORM_PRIORITY);
		mTickerGrabThread.start();
	}

	public void startKlineGrabThread() {
		if (mKlineGrabThread != null && mKlineGrabThread.isAlive()
				&& !mKlineGrabThread.isInterrupted()) {
			return;
		}
		mIsKlineGrabRunning = true;
		mKlineGrabThread = new DefaultThread(() -> {
			List<KlineInfo> _1minKlines, _15minKlines;
			long tickTime;
			boolean isUpdate1min, isUpdate15min;
			while (mIsKlineGrabRunning) {
				isUpdate1min = false;
				isUpdate15min = false;
				tickTime = System.currentTimeMillis();
				_1minKlines = JsonUtil.jsonToKlineList(mRestApi.futureKLine(mSymbol, mContractType,
						OKTypeConfig.KLINE_TYPE_1_MIN, "1000", null));
				if (_1minKlines != null && !_1minKlines.isEmpty()) {
					mStoreKlinesMap.putIfAbsent(OKTypeConfig.KLINE_TYPE_1_MIN, _1minKlines);
					if (CompareUtil.equal(_1minKlines.get(0),
							mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_1_MIN))) {
						mStoreKlinesMap.put(OKTypeConfig.KLINE_TYPE_1_MIN, _1minKlines);
						isUpdate1min = true;
					}
				}

				_15minKlines = JsonUtil.jsonToKlineList(mRestApi.futureKLine(mSymbol, mContractType,
						OKTypeConfig.KLINE_TYPE_15_MIN, "1000", null));
				if (_15minKlines != null && !_15minKlines.isEmpty()) {
					mStoreKlinesMap.putIfAbsent(OKTypeConfig.KLINE_TYPE_15_MIN, _15minKlines);
					if (CompareUtil.equal(_15minKlines.get(0),
							mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_15_MIN))) {
						mStoreKlinesMap.put(OKTypeConfig.KLINE_TYPE_15_MIN, _15minKlines);
						isUpdate15min = true;
					}
				}

				if (isUpdate1min || isUpdate15min) {
					// 短周期或者长周期的K线更新后，需要进行回调
					CallbackManager.get().onKlineInfoUpdated(OKTypeConfig.KLINE_TYPE_1_MIN,
							mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_1_MIN),
							OKTypeConfig.KLINE_TYPE_15_MIN,
							mStoreKlinesMap.get(OKTypeConfig.KLINE_TYPE_15_MIN));
				}

				sleepIfInTime("1min kline and 15min kline", tickTime, KLINE_GAP_TIME);

			}
		});
		mKlineGrabThread.setPriority(Thread.NORM_PRIORITY);
		mKlineGrabThread.start();
	}

	public void interruptTickerGrabThread() {
		mIsTickerGrabRunning = false;
		if (mTickerGrabThread != null && !mTickerGrabThread.isInterrupted()) {
			mTickerGrabThread.interrupt();
		}
	}

	public void interruptKlineGrabThread() {
		mIsKlineGrabRunning = false;
		if (mKlineGrabThread != null && !mKlineGrabThread.isInterrupted()) {
			mKlineGrabThread.interrupt();
		}
	}

	public void interruptDepthGrabThread() {
		mIsDepthGrabRunning = false;
		if (mDepthGrabThread != null && !mDepthGrabThread.isInterrupted()) {
			mDepthGrabThread.interrupt();
		}
	}

	public void startAll() {
		startDepthGrabThread();
		startKlineGrabThread();
		startTickerGrabThread();
	}

	public void stopAll() {
		interruptTickerGrabThread();
		interruptKlineGrabThread();
		interruptDepthGrabThread();
	}

}
