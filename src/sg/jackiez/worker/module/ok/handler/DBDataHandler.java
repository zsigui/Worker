package sg.jackiez.worker.module.ok.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.ref.WeakReference;
import java.util.List;

import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.FutureDataChangeCallback;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.thread.DefaultThread;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/22
 */
public class DBDataHandler implements FutureDataChangeCallback {

	private Thread mDBThread;
	private boolean mIsRunning;

	private WeakReference<List<KlineInfo>> mCache1minKlineData = new WeakReference<>(null);
	private WeakReference<List<KlineInfo>> mCache15minKlineData = new WeakReference<>(null);

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
		mIsRunning = true;
		mDBThread = new DefaultThread(() -> {
			while (mIsRunning) {
				DBManager.get().saveKline1minData(mCache1minKlineData.get());
				DBManager.get().saveKline15minData(mCache15minKlineData.get());
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
	}

	@Override
	public void onTickerDataUpdate(Ticker ticker) {

	}
}
