package sg.jackiez.worker.module.ok.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.AccountStateChangeCallback;
import sg.jackiez.worker.module.ok.manager.AccountManager;
import sg.jackiez.worker.module.ok.model.FutureHold4Fix;
import sg.jackiez.worker.module.ok.model.account.FutureContract;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class AccountDataGrabber implements AccountStateChangeCallback {

	private static final String TAG = "AccountDataGrabber";

	private static final int MAX_RETRY_TIME = 3;

	private final Object mLockObj = new Object();

	private boolean mIsRunning = false;
	private Thread mGrabDataThread;
	private IFutureRestApi mFutureRestApi;

	private String mSymbol;
	private String mContractType;

	public AccountDataGrabber() {
		this(OKTypeConfig.SYMBOL_EOS, OKTypeConfig.CONTRACT_TYPE_QUARTER, new FutureRestApiV1());
	}

	public AccountDataGrabber(String symbol,
	                          String contractType,
	                          IFutureRestApi futureRestApi) {
		mFutureRestApi = futureRestApi;
		mSymbol = symbol;
		mContractType = contractType;
	}

	private boolean isGrabAccountDataThreadAlive() {
		return mGrabDataThread != null && mGrabDataThread.isAlive()
				&& !mGrabDataThread.isInterrupted();
	}

	public void startGrabAccountDataThread() {
		if (isGrabAccountDataThreadAlive()) {
			// 已经在跑,直接唤醒即可
			synchronized (mLockObj) {
				mLockObj.notify();
			}
			return;
		}

		mIsRunning = true;
		mGrabDataThread = new DefaultThread(() -> {
			ArrayList<FutureHold4Fix> holdList;
			Map<String, FutureContract> userInfo;

			int retryTime;
			long startTime;
			while (mIsRunning) {

				startTime = System.currentTimeMillis();
				retryTime = MAX_RETRY_TIME;
				holdList = null;
				while (holdList == null && retryTime-- > 0) {
					holdList = JsonUtil.jsonToSuccessDataForFuture(
							mFutureRestApi.futurePositionForFix(mSymbol, mContractType),
							"holding", new TypeReference<ArrayList<FutureHold4Fix>>() {
							});
				}

				retryTime = MAX_RETRY_TIME;
				userInfo = null;
				while (userInfo == null && retryTime-- > 0) {
					userInfo = JsonUtil.jsonToSuccessDataForFuture(mFutureRestApi.futureUserInfoForFix(),
							"info", new TypeReference<HashMap<String, FutureContract>>() {
							});
				}

				SLogUtil.v(TAG, "startGrabAccountDataThread this running total spend time: "
						+ (System.currentTimeMillis() - startTime) + " ms. userInfo == null ? "
						+ (userInfo == null) + ", holdList == null ? " + (holdList == null));

				if (userInfo != null && holdList != null) {
					AccountManager.get().putAccountInfo(userInfo, holdList);
				}

				synchronized (mLockObj) {
					try {
						mLockObj.wait(30_000);
					} catch (InterruptedException e) {
						SLogUtil.v(e);
					}
				}
			}
		});
		mGrabDataThread.setPriority(Thread.MAX_PRIORITY);
		mGrabDataThread.start();
	}

	public void stopGrabAccountDataThread() {
		if (isGrabAccountDataThreadAlive()) {
			mIsRunning = false;
			synchronized (mLockObj) {
				mLockObj.notify();
			}
			mGrabDataThread.interrupt();
		}
	}

	@Override
	public void onAccountInfoUpdated() {
	}

	@Override
	public void onAccountInfoOutdated() {
		startGrabAccountDataThread();
	}
}
