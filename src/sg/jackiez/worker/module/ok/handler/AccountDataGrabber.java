package sg.jackiez.worker.module.ok.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.callback.SimpleCallback;
import sg.jackiez.worker.module.ok.OKError;
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
import sg.jackiez.worker.utils.ExecutorUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class AccountDataGrabber implements AccountStateChangeCallback {

	private static final String TAG = "AccountDataGrabber";

	private static final int MAX_RETRY_TIME = 3;
	private static final int DEFAULT_FETCH_GAP_TIME = 5_000;

	private final Object mLockObj = new Object();

	private boolean mIsRunning = false;
	private Thread mGrabDataThread;

	public AccountDataGrabber() {
	}

    public List<FutureOrder> getTotalOrderListSync(String instrumentId) {

        List<FutureOrder> totalList = new ArrayList<>();
        // 获取完全未成交的订单信息
        getOrdersByStateSync(instrumentId,
                OKTypeConfig.STATUS_NOT_TRANSACT, totalList);
        // 获取部分成交的订单信息
        getOrdersByStateSync(instrumentId,
                OKTypeConfig.STATUS_PART_TRANSACT, totalList);
        // 获取已完全成交的订单信息
        getOrdersByStateSync(instrumentId,
                OKTypeConfig.STATUS_FULL_TRANSACT, totalList);
        // 获取已取消订单信息
        getOrdersByStateSync(instrumentId,
                OKTypeConfig.STATUS_CANCELED, totalList);

		SLogUtil.i(TAG, "getTotalOrderListSync: total order list = " + totalList.size());
		DBManager.get().updateMultiTradeState(totalList);
		return totalList;
    }

	public void getTotalOrderList(String instrumentId, SimpleCallback<List<FutureOrder>> callback) {
		if (callback == null) {
			return;
		}

		ExecutorUtil.getCachedExecutor().submit(() -> {
			List<FutureOrder> orderList = getTotalOrderListSync(instrumentId);
			if (orderList == null) {
				callback.onFail(OKError.E_NULL, OKError.STR_E_NULL);
			} else if (orderList.isEmpty()) {
				callback.onFail(OKError.E_EMPTY, OKError.STR_E_EMPTY);
			} else {
				callback.onSuccess(orderList);
			}
		});
	}

    public FutureOrder getOrderIfContainByOrderIdSync(String instrumentId, String orderId, int status) {
		List<FutureOrder> result = getOrdersByStateSync(instrumentId, status, null);
		if (result != null) {
			for (FutureOrder order : result) {
				if (order.order_id.equals(orderId)) {
					return order;
				}
			}
		}
		return null;
	}

	public void getOrderIfContainByOrderId(String instrumentId, String orderId, int status,
												  SimpleCallback<FutureOrder> callback) {
		if (callback == null) {
			return;
		}

		ExecutorUtil.getCachedExecutor().submit(() -> {
			FutureOrder order = getOrderIfContainByOrderIdSync(instrumentId, orderId, status);
			if (order == null) {
				callback.onFail(OKError.E_NULL, OKError.STR_E_NULL);
			} else {
				callback.onSuccess(order);
			}
		});
	}

    public List<FutureOrder> getOrdersByStateSync(String instrumentId, int status,
												  List<FutureOrder> store) {
        List<FutureOrder> result = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getOrderList(instrumentId,
                String.valueOf(status), "1", null, "100"),
                "order_info",
                new TypeReference<List<FutureOrder>>() {
                });
        if (result != null && store != null) {
			SLogUtil.i(TAG, "getOrdersByStateSync: status = " + status + ", size = " + result.size());
            store.addAll(result);
        }
        return result;
    }

    public void getOrdersByState(String instrumentId, int status, List<FutureOrder> store,
								 SimpleCallback<List<FutureOrder>> callback) {
		if (callback == null) {
			return;
		}

		ExecutorUtil.getCachedExecutor().submit(() -> {
			List<FutureOrder> orderList = getOrdersByStateSync(instrumentId, status, store);
			if (orderList == null) {
				callback.onFail(OKError.E_NULL, OKError.STR_E_NULL);
			} else if (orderList.isEmpty()) {
				callback.onFail(OKError.E_EMPTY, OKError.STR_E_EMPTY);
			} else {
				callback.onSuccess(orderList);
			}
		});
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
						mLockObj.wait(DEFAULT_FETCH_GAP_TIME);
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
			userInfo = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getUserInfo(),
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
			userInfo = JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.getUserInfo(),
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
