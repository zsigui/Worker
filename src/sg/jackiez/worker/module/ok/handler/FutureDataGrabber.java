package sg.jackiez.worker.module.ok.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.InstrumentInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.module.ok.model.resp.RespTicker;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.module.ok.utils.CompareUtil;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.module.ok.utils.ReqUtil;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.common.CollectionUtil;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class FutureDataGrabber {

	private static final String TAG = "FutureDataGrabber";

	private IFutureRestApi mRestApi;
	private FutureRestApiV3 mRestApiV3;
	private String mSymbol;
	private String mContractType;
	private String mInstrumentId;

	private Ticker mTicker;
	private HashMap<String, Long> mStoreKlinesLastDataTimeMap = new HashMap<>(2);
	private HashMap<String, List<KlineInfo>> mStoreKlinesMap = new HashMap<>(2);
	private DepthInfo mDepthInfo;
	private List<TradeHistoryItem> mLastTradeHistory;

	private final int KLINE_GAP_TIME = 500;
	private final int TICKER_GAP_TIME = 500;
	private final int DEPTH_GAP_TIME = 500;
	private final int TRADE_GAP_TIME = 300;
	// 一年时间
	private static final long ONE_YEAR_TIME_IN_MILLIS = 365L * 7 * 24 * 60 * 60 * 1000;
	private static final int KLINE_LIMIT_SIZE = 1000;

	private boolean mIsTickerGrabRunning = false;
	private boolean mIsKlineGrabRunning = false;
	private boolean mIsDepthGrabRunning = false;
	private boolean mIsTradeGrabRunning = false;
	private Thread mTickerGrabThread;
	private Thread mKlineGrabThread;
	private Thread mDepthGrabThread;
	private Thread mTradeGrabThread;


	public FutureDataGrabber() {
		this("eos_usd", OKTypeConfig.CONTRACT_TYPE_QUARTER, new FutureRestApiV1());
	}

	public FutureDataGrabber(String symbol, String contractType, IFutureRestApi restApi) {
		mSymbol = symbol;
		mContractType = contractType;
		mRestApi = restApi;
		mRestApiV3 = new FutureRestApiV3();
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

	public HashMap<String, List<KlineInfo>> getKlineInfoMap() {
		return mStoreKlinesMap;
	}

	public DepthInfo getDepthInfo() {
		return mDepthInfo;
	}

	public Ticker getTicker() {
		return mTicker;
	}

	private void judgeInstrumentId() {
		if (mInstrumentId == null) {
			initInstrumentId();
		}
	}

	public void initInstrumentId() {
		String resp = ReqUtil.retry(3, () -> mRestApiV3.getInstruments());
		if (resp == null) {
			throw new RuntimeException("get no instrument data.");
		}

		List<InstrumentInfo> instrumentInfos = JsonUtil.jsonToSuccessDataForFuture(resp,
				new TypeReference<List<InstrumentInfo>>() {
				});
		if (instrumentInfos == null || instrumentInfos.isEmpty()) {
			throw new RuntimeException("translate json data error. src = " + resp);
		}

		String currency = mSymbol.replace("_", "-").toUpperCase();
		int i = 0;
		for (InstrumentInfo info : instrumentInfos) {
			if (info.instrument_id.startsWith(currency)) {
				if ((i == 0 && mContractType.equals(OKTypeConfig.CONTRACT_TYPE_THIS_WEEK))
						|| (i == 1 && mContractType.equals(OKTypeConfig.CONTRACT_TYPE_NEXT_WEEK))
						|| (i == 2 && mContractType.equals(OKTypeConfig.CONTRACT_TYPE_QUARTER))) {
					mInstrumentId = info.instrument_id;
					break;
				}
				i++;
			}
		}

		SLogUtil.i(TAG, "initInstrumentId : " + mInstrumentId);
	}

	/**
	 * 获取合约ID,需要在调用{@link #initInstrumentId()}成功之后
	 */
	public String getInstrumentId() {
		return mInstrumentId;
	}

	public void startDepthGrabThread() {
		judgeInstrumentId();
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
						SLogUtil.i(TAG, "startDepthGrabThread() 获取到新行情数据, 距上次时间: "
								+ (nowTime - lastTime) + " ms");
						depthInfoOld = depthInfoNew;
						mDepthInfo = depthInfoNew;
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
		judgeInstrumentId();
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
							SLogUtil.i(TAG, "startTickerGrabThread() 获取到新行情数据, 距上次时间: "
									+ (nowTime - lastTime) + " ms");
							lastTime = nowTime;
							tickerOld = tickerNew;
							mTicker = tickerNew.ticker;
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

	public void startTradeGrabThread() {
		judgeInstrumentId();
		if (mTradeGrabThread != null && mTradeGrabThread.isAlive()
				&& !mTradeGrabThread.isInterrupted()) {
			return;
		}

		mIsTradeGrabRunning = true;
		mTradeGrabThread = new DefaultThread(() -> {
			List<TradeHistoryItem> tradeHistory;
			long lastTime = System.currentTimeMillis();
			long nowTime;
			long tickTime;
			while (mIsTradeGrabRunning) {
				tickTime = System.currentTimeMillis();
				try {
					tradeHistory = JsonUtil.jsonToSuccessDataForFuture(mRestApiV3.getTradeHistory(mInstrumentId, "",
							"", ""),
							new TypeReference<List<TradeHistoryItem>>() {
							});
					if (tradeHistory != null && !tradeHistory.isEmpty()) {
						mLastTradeHistory = tradeHistory;
						CallbackManager.get().onGetTradeHistory(tradeHistory);
						nowTime = System.currentTimeMillis();
						SLogUtil.i(TAG, "startTradeGrabThread() 数据更新, 距上次时间: "
								+ (nowTime - lastTime) + " ms");
						lastTime = nowTime;
					}
				} catch (Throwable e) {
					SLogUtil.v(TAG, e);
				} finally {
					sleepIfInTime("trade grab", tickTime, TRADE_GAP_TIME);
				}
			}
		});
		mTradeGrabThread.setPriority(Thread.NORM_PRIORITY);
		mTradeGrabThread.start();
	}

	public void startKlineGrabThread() {
		judgeInstrumentId();
		if (mKlineGrabThread != null && mKlineGrabThread.isAlive()
				&& !mKlineGrabThread.isInterrupted()) {
			return;
		}

		mIsKlineGrabRunning = true;
		mKlineGrabThread = new DefaultThread(() -> {
			long lastTime = System.currentTimeMillis();
			long tickTime;
			long nowTime;
			boolean isUpdate1min, isUpdate15min;
			while (mIsKlineGrabRunning) {
				tickTime = System.currentTimeMillis();
				isUpdate1min = updateKlineForTime(OKTypeConfig.KLINE_TYPE_1_MIN);
				isUpdate15min = updateKlineForTime(OKTypeConfig.KLINE_TYPE_15_MIN);

				if (isUpdate1min || isUpdate15min) {
					// 短周期或者长周期的K线更新后，需要进行回调
					nowTime = System.currentTimeMillis();
					SLogUtil.i(TAG,
							"startKlineGrabThread K线数据有更新: 1min = " + isUpdate1min + ", 15min = " + isUpdate15min
							+ ", 距离上次更新时间：" + (nowTime - lastTime) + "ms");
					lastTime = nowTime;
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

	private boolean updateKlineForTime(String ktime) {
		long curTime = System.currentTimeMillis();
		List<KlineInfo> tmp = mStoreKlinesMap.get(ktime);
		String start = DateUtil.formatISOTime(tmp == null || tmp.isEmpty() ? (curTime - ONE_YEAR_TIME_IN_MILLIS) :
				tmp.get(tmp.size() - 1).time);
		List<KlineInfo> _timeKlines = JsonUtil.jsonToKlineList(mRestApiV3.getKlineInfo(
				mInstrumentId, start, DateUtil.formatISOTime(curTime), ktime));

		if (_timeKlines != null && !_timeKlines.isEmpty()) {
			SLogUtil.d(TAG, "updateKlineForTime(" + ktime + ") : size = " + _timeKlines.size() + ", value = " + _timeKlines);
			int updateSize = 0;
			if (tmp != null) {
				long lastTime = mStoreKlinesLastDataTimeMap.getOrDefault(ktime, 0L);
				for (KlineInfo info : _timeKlines) {
					if (info.time > lastTime) {
						tmp.add(info);
						updateSize++;
					}
				}
			} else {
				tmp = _timeKlines;
				updateSize = tmp.size();
			}

			if (updateSize > 0) {
				// 有单独数据添加
				mStoreKlinesLastDataTimeMap.put(ktime, tmp.get(tmp.size() - 1).time);
				mStoreKlinesMap.put(ktime, CollectionUtil.limit(tmp, KLINE_LIMIT_SIZE, true));
				return true;
			}
		}
		return false;
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

	public void interruptTradeGrabThread() {
		mIsTradeGrabRunning = false;
		if (mTradeGrabThread != null && !mTradeGrabThread.isInterrupted()) {
			mTradeGrabThread.interrupt();
		}
	}

	public void startAll() {
//		startDepthGrabThread();
		startKlineGrabThread();
//		startTickerGrabThread();
		startTradeGrabThread();
	}

	public void stopAll() {
//		interruptTickerGrabThread();
		interruptKlineGrabThread();
//		interruptDepthGrabThread();
		interruptTradeGrabThread();
	}

}
