package sg.jackiez.worker.module.ok.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.model.FutureHold;
import sg.jackiez.worker.module.ok.model.account.FutureContract;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.annotations.NonNull;

/**
 * 账号信息管理
 *
 * @Author JackieZ
 * @Date Created on 2018/10/1
 */
public class AccountManager {

	private static final String TAG = "AccountManager";

	private static final class SingletonHolder {
		static final AccountManager sInstance = new AccountManager();
	}

	public static AccountManager get() {
		return SingletonHolder.sInstance;
	}

	/**
	 * 账户类型根据网页设置,默认是逐仓
	 */
	private byte mAccountType = OKTypeConfig.ACCOUNT_TYPE_4_FIX;
	/**
	 * 当前合约信息
	 */
	private HashMap<String, FutureContract> mCoinContractMap = new HashMap<>();
	/**
	 * 当前持有的合约单信息, key = symbol + contract_id
	 */
	private HashMap<String, FutureHold> mFutureHoldMap = new HashMap<>();

	private boolean mNeedUpdateInfo = false;

	public byte getAccountType() {
		return mAccountType;
	}

	public FutureContract getContractByCoin(String coinName) {
		return mCoinContractMap.get(coinName);
	}

	public FutureContract getEosContract() {
		return getContractByCoin("eos");
	}

	public FutureHold getHold(String key) {
		return mFutureHoldMap.get(key);
	}

	public void putAccountInfo(@NonNull Map<String, FutureContract> contractMap,
	                           @NonNull List<? extends FutureHold> futureHoldList) {
		if (contractMap == null || futureHoldList == null) {
			SLogUtil.d(TAG, "putAccountInfo params are not null.");
			return;
		}
		mCoinContractMap.clear();
		futureHoldList.clear();
		mCoinContractMap.putAll(contractMap);
		for (FutureHold hold : futureHoldList) {
			mFutureHoldMap.put(hold.symbol + hold.contract_id, hold);
		}
		setNeedUpdateInfo(false);
		CallbackManager.get().onAccountInfoUpdated();
	}

	public boolean isNeedUpdateInfo() {
		return mNeedUpdateInfo;
	}

	public void setNeedUpdateInfo(boolean needUpdateInfo) {
		if (!mNeedUpdateInfo && needUpdateInfo) {
			SLogUtil.d(TAG, "setNeedUpdateInfo state change, call update!");
			mNeedUpdateInfo = true;
			CallbackManager.get().onAccountInfoOutdated();
		} else {
			mNeedUpdateInfo = needUpdateInfo;
		}
	}
}
