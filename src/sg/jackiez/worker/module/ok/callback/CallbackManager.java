package sg.jackiez.worker.module.ok.callback;

import java.util.List;

import io.netty.util.internal.ConcurrentSet;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class CallbackManager implements
		FutureDataChangeCallback,
		AccountStateChangeCallback,
		VendorResultCallback {

	private static final String TAG = "CallbackManager";

	private static final class SingletonHolder {
		static final CallbackManager sInstance = new CallbackManager();
	}

	public static CallbackManager get() {
		return SingletonHolder.sInstance;
	}

	private ConcurrentSet<FutureDataChangeCallback> mFutureDataChangedCallbacks = new ConcurrentSet<>();
	private ConcurrentSet<AccountStateChangeCallback> mAccountStateChangeCallbacks = new ConcurrentSet<>();
	private ConcurrentSet<VendorResultCallback> mVendorResultCallbacks = new ConcurrentSet<>();

	/*======================== 添加/移除回调Start ============================*/

	public void addFutureDataChangeCallback(FutureDataChangeCallback callback) {
		if (callback == null) {
			return;
		}
		mFutureDataChangedCallbacks.add(callback);
	}

	public void removeFutureDataChangeCallback(FutureDataChangeCallback callback) {
		if (callback == null) {
			return;
		}
		mFutureDataChangedCallbacks.remove(callback);
	}

	public void addAccountStateChangeCallback(AccountStateChangeCallback callback) {
		if (callback == null) {
			return;
		}
		mAccountStateChangeCallbacks.add(callback);
	}

	public void removeAccountStateChangeCallback(AccountStateChangeCallback callback) {
		if (callback == null) {
			return;
		}
		mAccountStateChangeCallbacks.remove(callback);
	}

	public void addVendorResultCallback(VendorResultCallback callback) {
		if (callback == null) {
			return;
		}
		mVendorResultCallbacks.add(callback);
	}

	public void removeVendorResultCallback(VendorResultCallback callback) {
		if (callback == null) {
			return;
		}
		mVendorResultCallbacks.remove(callback);
	}

	/*======================== 添加/移除回调End ============================*/

	/*======================== 调用实现Start ============================*/

	@Override
	public void onDepthUpdated(DepthInfo depthInfo) {
		SLogUtil.v(TAG, "onDepthUpdated");
		for (FutureDataChangeCallback callback : mFutureDataChangedCallbacks) {
			callback.onDepthUpdated(depthInfo);
		}
	}

	@Override
	public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos,
	                               String longTimeType, List<KlineInfo> longKlineInfos) {
		SLogUtil.v(TAG, "onKlineInfoUpdated");
		for (FutureDataChangeCallback callback : mFutureDataChangedCallbacks) {
			callback.onKlineInfoUpdated(shortTimeType, shortKlineInfos, longTimeType, longKlineInfos);
		}
	}

	@Override
	public void onGetUpdatedKlineInfo(String timeType, List<KlineInfo> updated) {
		SLogUtil.v(TAG, "onGetUpdatedKlineInfo");
		for (FutureDataChangeCallback callback : mFutureDataChangedCallbacks) {
			callback.onGetUpdatedKlineInfo(timeType, updated);
		}
	}

	@Override
	public void onTickerDataUpdate(Ticker ticker) {
		SLogUtil.v(TAG, "onTickerDataUpdate");
		for (FutureDataChangeCallback callback : mFutureDataChangedCallbacks) {
			callback.onTickerDataUpdate(ticker);
		}
	}

	@Override
	public void onGetTradeHistory(List<TradeHistoryItem> tradeHistory) {
		SLogUtil.v(TAG, "onGetTradeHistory");
		for (FutureDataChangeCallback callback : mFutureDataChangedCallbacks) {
			callback.onGetTradeHistory(tradeHistory);
		}
	}

	@Override
	public void onAccountInfoUpdated() {
		SLogUtil.v(TAG, "onAccountInfoUpdated");
		for (AccountStateChangeCallback callback : mAccountStateChangeCallbacks) {
			callback.onAccountInfoUpdated();
		}
	}

	@Override
	public void onAccountInfoOutdated() {
		SLogUtil.v(TAG, "onAccountInfoOutdated");
		for (AccountStateChangeCallback callback : mAccountStateChangeCallbacks) {
			callback.onAccountInfoOutdated();
		}
	}

	@Override
	public void onTradeSuccess(String clientOId, String orderId, String instrumentId) {
		SLogUtil.v(TAG, "onTradeSuccess: clientOId = " + clientOId + ", orderId = " + orderId
				+ ", instrumentId = " + instrumentId);
		for (VendorResultCallback callback : mVendorResultCallbacks) {
			callback.onTradeSuccess(clientOId, orderId, instrumentId);
		}
	}

	@Override
	public void onTradeFail(int errCode, String errMsg) {
		SLogUtil.v(TAG, "onTradeSuccess: errCode = " + errCode + ", errMsg = " + errMsg);
		for (VendorResultCallback callback : mVendorResultCallbacks) {
			callback.onTradeFail(errCode, errMsg);
		}
	}

	@Override
	public void onCancelOrderSuccess(String orderId, String instrumentId) {
		SLogUtil.v(TAG, "onCancelOrderSuccess: orderId = " + orderId
				+ ", instrumentId = " + instrumentId);
		for (VendorResultCallback callback : mVendorResultCallbacks) {
			callback.onCancelOrderSuccess(orderId, instrumentId);
		}
	}

	@Override
	public void onCancelOrderFail(int errCode, String errMsg) {
		SLogUtil.v(TAG, "onCancelOrderFail: errCode = " + errCode + ", errMsg = " + errMsg);
		for (VendorResultCallback callback : mVendorResultCallbacks) {
			callback.onCancelOrderFail(errCode, errMsg);
		}
	}

	/*======================== 调用实现End ============================*/
}
