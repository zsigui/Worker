package sg.jackiez.worker.module.ok.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.module.ok.utils.CompareUtil;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.common.CollectionUtil;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class FutureDataGrabber {

	private static final String TAG = "FutureDataGrabber";

	private String mInstrumentId;

	private Ticker mTicker;
	private HashMap<String, List<KlineInfo>> mStoreKlinesMap = new HashMap<>(2);
	private DepthInfo mDepthInfo;
	private List<TradeHistoryItem> mLastTradeHistory;

	private final int KLINE_GAP_TIME = 500;
	private final int TICKER_GAP_TIME = 500;
	private final int DEPTH_GAP_TIME = 500;
	private final int TRADE_GAP_TIME = 300;
	private final boolean IS_SORT_FROM_SMALL = false;
	// 一年时间
	private static final long ONE_YEAR_TIME_IN_MILLIS = 365L * 24 * 60 * 60 * 1000;
	private static final int KLINE_LIMIT_SIZE = 1000;

	private boolean mIsTickerGrabRunning = false;
	private boolean mIsKlineGrabRunning = false;
	private boolean mIsDepthGrabRunning = false;
	private boolean mIsTradeGrabRunning = false;
	private Thread mTickerGrabThread;
	private Thread mKlineGrabThread;
	private Thread mDepthGrabThread;
	private Thread mTradeGrabThread;

	public FutureDataGrabber(String instrumentId) {
		mInstrumentId = instrumentId;
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
					depthInfoNew = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getDepthInfo(mInstrumentId, "100"),
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
		if (mTickerGrabThread != null && mTickerGrabThread.isAlive()
				&& !mTickerGrabThread.isInterrupted()) {
			return;
		}

		mIsTickerGrabRunning = true;
		mTickerGrabThread = new DefaultThread(() -> {
			Ticker tickerNew, tickerOld = null;
			long lastTime = System.currentTimeMillis();
			long nowTime;
			long tickTime;
			while (mIsTickerGrabRunning) {
				tickTime = System.currentTimeMillis();
				try {
					tickerNew = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getTickerInfo(mInstrumentId),
							new TypeReference<Ticker>() {
							});
					if (tickerNew != null) {
						if (tickerOld == null || !CompareUtil.equal(tickerNew, tickerOld)) {
							CallbackManager.get().onTickerDataUpdate(tickerNew);
							nowTime = System.currentTimeMillis();
							SLogUtil.i(TAG, "startTickerGrabThread() 获取到新行情数据, 距上次时间: "
									+ (nowTime - lastTime) + " ms");
							lastTime = nowTime;
							tickerOld = tickerNew;
							mTicker = tickerNew;
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
					tradeHistory = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getTradeHistory(mInstrumentId, "",
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
				tmp.get(IS_SORT_FROM_SMALL ? tmp.size() - 1 : 0).time);
		List<KlineInfo> _timeKlines = JsonUtil.jsonToKlineList(FutureRestApiV3.getKlineInfo(
				mInstrumentId, start, DateUtil.formatISOTime(curTime), ktime), IS_SORT_FROM_SMALL);
		List<KlineInfo> updateKlines = new LinkedList<>();
		if (_timeKlines != null && !_timeKlines.isEmpty()) {
			SLogUtil.d(TAG, "updateKlineForTime(" + ktime + ") : size = " + _timeKlines.size() + ", value = " + _timeKlines);
			int updateSize = 0;
			if (tmp != null) {
				final KlineInfo lastItem = tmp.get(IS_SORT_FROM_SMALL ? tmp.size() - 1 : 0);
				final long lastTime = lastItem.time;
				updateKlines.clear();
				if (IS_SORT_FROM_SMALL) {
					for (KlineInfo info : _timeKlines) {
						if (info.time > lastTime) {
							tmp.add(info);
							updateKlines.add(info);
							updateSize++;
						} else if (info.time == lastTime && lastItem.volume != info.volume) {
							tmp.remove(tmp.size() - 1);
							tmp.add(info);
							updateKlines.add(info);
							updateSize++;
						}
					}
				} else {
					KlineInfo info;
					for (int i = _timeKlines.size() - 1; i >= 0; i--) {
						info = _timeKlines.get(i);
						if (info.time > lastTime) {
							tmp.add(0, info);
							updateKlines.add(info);
							updateSize++;
						} else if (info.time == lastTime && lastItem.volume != info.volume) {
							tmp.remove(0);
							tmp.add(0, info);
							updateKlines.add(info);
							updateSize++;
						}
					}
				}
			} else {
				tmp = _timeKlines;
				updateKlines.addAll(tmp);
				updateSize = tmp.size();
			}

			SLogUtil.d(TAG, "updateKlineForTime(" + ktime + ") : updateSize = " + updateSize);
			if (updateSize > 0) {
				// 有单独数据添加
				mStoreKlinesMap.put(ktime, CollectionUtil.limit(tmp, KLINE_LIMIT_SIZE, false));
				CallbackManager.get().onGetUpdatedKlineInfo(ktime, updateKlines);
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
