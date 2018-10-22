package sg.jackiez.worker.module.ok.handler;

import java.lang.ref.WeakReference;
import java.util.List;

import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.FutureDataChangeCallback;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.thread.DefaultThread;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/22
 */
public class DBDataHandler implements FutureDataChangeCallback {

	private static final String TAG = "DBDataHandler";

	private Thread mDBThread;
	private boolean mIsRunning;

	private WeakReference<List<KlineInfo>> mCache1minKlineData = new WeakReference<>(null);
	private WeakReference<List<KlineInfo>> mCache15minKlineData = new WeakReference<>(null);
	private WeakReference<List<TradeHistoryItem>> mCacheTradeHistoryList = new WeakReference<>(null);

	public DBDataHandler() {
		CallbackManager.get().addFutureDataChangeCallback(this);
	}

	public void startDBThread() {
		if (mDBThread != null && mDBThread.isAlive()
				&& !mDBThread.isInterrupted()) {
			synchronized (mDBThread) {
				mDBThread.notify();
			}
			return;
		}
		SLogUtil.i(TAG, "startDBThread");
		mIsRunning = true;
		mDBThread = new DefaultThread(() -> {
			while (mIsRunning) {
				SLogUtil.v(TAG, "try to store new data.");
				int _1minSize = DBManager.get().saveKline1minData(mCache1minKlineData.get());
				int _15minSize = DBManager.get().saveKline15minData(mCache15minKlineData.get());
				int _tradeSize = DBManager.get().saveTradeHistory(mCacheTradeHistoryList.get());
				SLogUtil.v(TAG, "1min K线最新写入数据数量: " + _1minSize);
				SLogUtil.v(TAG, "15min K线最新写入数据数量: " + _15minSize);
				SLogUtil.v(TAG, "成交历史数据最新写入数据数量: " + _tradeSize);
				mCache1minKlineData.clear();
				mCache15minKlineData.clear();
				mCacheTradeHistoryList.clear();
				synchronized (mDBThread) {
					try {
						mDBThread.wait();
					} catch (InterruptedException ignored) {
					}
				}
			}
		});
		mDBThread.setPriority(Thread.NORM_PRIORITY);
		mDBThread.start();
	}

	public void stopDBThread() {
		mIsRunning = false;
		if (mDBThread != null && mDBThread.isAlive()) {
			synchronized (mDBThread) {
				mDBThread.notify();
			}
		}
		mDBThread = null;
	}

	@Override
	public void onDepthUpdated(DepthInfo depthInfo) {
		// ignored
	}

	@Override
	public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos, String longTimeType,
	                               List<KlineInfo> longKlineInfos) {
		mCache1minKlineData = new WeakReference<>(shortKlineInfos);
		mCache15minKlineData = new WeakReference<>(longKlineInfos);
		startDBThread();
	}

	@Override
	public void onTickerDataUpdate(Ticker ticker) {

	}

	@Override
	public void onGetTradeHistory(List<TradeHistoryItem> tradeHistory) {
		mCacheTradeHistoryList = new WeakReference<>(tradeHistory);
		startDBThread();
	}
}
