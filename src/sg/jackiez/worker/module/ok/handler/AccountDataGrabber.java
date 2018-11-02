package sg.jackiez.worker.module.ok.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.AccountStateChangeCallback;
import sg.jackiez.worker.module.ok.manager.AccountManager;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.module.ok.manager.PrecursorManager;
import sg.jackiez.worker.module.ok.model.FutureOrder;
import sg.jackiez.worker.module.ok.model.FuturePosition;
import sg.jackiez.worker.module.ok.model.FuturePosition4Fix;
import sg.jackiez.worker.module.ok.model.account.FutureContract4FixV3;
import sg.jackiez.worker.module.ok.model.account.FutureContractV3;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class AccountDataGrabber implements AccountStateChangeCallback {

	private static final String TAG = "AccountDataGrabber";

	private static final int MAX_RETRY_TIME = 3;

	private final Object mLockObj = new Object();

	private boolean mIsRunning = false;
	private Thread mGrabDataThread;

	public AccountDataGrabber() {
	}

    public List<FutureOrder> getTotalOrderList(String instrumentId) {

        List<FutureOrder> totalList = new ArrayList<>();
        // 获取完全未成交的订单信息
        getOrdersByState(instrumentId,
                OKTypeConfig.STATUS_NOT_TRANSACT, totalList);
        // 获取部分成交的订单信息
        getOrdersByState(instrumentId,
                OKTypeConfig.STATUS_PART_TRANSACT, totalList);
        // 获取已完全成交的订单信息
        getOrdersByState(instrumentId,
                OKTypeConfig.STATUS_FULL_TRANSACT, totalList);
        // 获取已取消订单信息
        getOrdersByState(instrumentId,
                OKTypeConfig.STATUS_CANCELED, totalList);

		SLogUtil.i(TAG, "getTotalOrderList: total order list = " + totalList.size());
		DBManager.get().updateMultiTradeState(totalList);
		return totalList;
    }

    public FutureOrder getOrderIfContainByOrderId(String instrumentId, String orderId, int status) {
		List<FutureOrder> result = getOrdersByState(instrumentId, status, null);
		if (result != null) {
			for (FutureOrder order : result) {
				if (order.order_id.equals(orderId)) {
					return order;
				}
			}
		}
		return null;
	}

    private List<FutureOrder> getOrdersByState(String instrumentId, int status,
                                               List<FutureOrder> store) {
        List<FutureOrder> result = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getOrderList(instrumentId,
                String.valueOf(status), "0", null, "100"),
                "order_info",
                new TypeReference<List<FutureOrder>>() {
                });
        if (result != null && store != null) {
			SLogUtil.i(TAG, "getOrdersByState: status = " + status + ", size = " + result.size());
            store.addAll(result);
        }
        return result;
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
			while (mIsRunning) {

				if (PrecursorManager.get().isFixedAccount()) {
					getFixedData();
				} else {
					getCrossedData();
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

	/**
	 * 获取全仓相关数据
	 */
	private void getCrossedData() {
		long startTime;
		int retryTime;
		ArrayList<FuturePosition> holdList;
		Map<String, FutureContractV3> userInfo;
		startTime = System.currentTimeMillis();
		retryTime = MAX_RETRY_TIME;
		holdList = null;
		while (holdList == null && retryTime-- > 0) {
			holdList = JsonUtil.jsonToSuccessDataForFuture(
					FutureRestApiV3.getAllPositionInfo(),
					"holding", new TypeReference<ArrayList<FuturePosition>>() {
					});
		}

		retryTime = MAX_RETRY_TIME;
		userInfo = null;
		while (userInfo == null && retryTime-- > 0) {
			userInfo = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getAllPositionInfo(),
					"info", new TypeReference<HashMap<String, FutureContractV3>>() {
					});
		}

		SLogUtil.v(TAG, "startGrabAccountDataThread.getCrossedData this running total spend time: "
				+ (System.currentTimeMillis() - startTime) + " ms. userInfo == null ? "
				+ (userInfo == null) + ", holdList == null ? " + (holdList == null));

		if (userInfo != null && holdList != null) {
			AccountManager.get().putCrossedAccountInfo(userInfo, holdList);
		}
	}

	/**
	 * 获取逐仓相关数据
	 */
	private void getFixedData() {
		long startTime;
		int retryTime;
		ArrayList<FuturePosition4Fix> holdList;
		Map<String, FutureContract4FixV3> userInfo;
		startTime = System.currentTimeMillis();
		retryTime = MAX_RETRY_TIME;
		holdList = null;
		while (holdList == null && retryTime-- > 0) {
			holdList = JsonUtil.jsonToSuccessDataForFuture(
					FutureRestApiV3.getAllPositionInfo(),
					"holding", new TypeReference<ArrayList<FuturePosition4Fix>>() {
					});
		}

		retryTime = MAX_RETRY_TIME;
		userInfo = null;
		while (userInfo == null && retryTime-- > 0) {
			userInfo = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getAllPositionInfo(),
					"info", new TypeReference<HashMap<String, FutureContract4FixV3>>() {
					});
		}

		SLogUtil.v(TAG, "startGrabAccountDataThread.getFixedData this running total spend time: "
				+ (System.currentTimeMillis() - startTime) + " ms. userInfo == null ? "
				+ (userInfo == null) + ", holdList == null ? " + (holdList == null));

		if (userInfo != null && holdList != null) {
			AccountManager.get().putFixedAccountInfo(userInfo, holdList);
		}
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
