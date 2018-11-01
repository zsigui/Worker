package sg.jackiez.worker.module.ok.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.model.FuturePosition;
import sg.jackiez.worker.module.ok.model.FuturePosition4Fix;
import sg.jackiez.worker.module.ok.model.account.FutureContract4FixV3;
import sg.jackiez.worker.module.ok.model.account.FutureContractV3;
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
     * 当前逐仓合约信息
     */
    private HashMap<String, FutureContract4FixV3> mFutureFixedContractMap = new HashMap<>();
    /**
     * 当前持有的逐仓合约单信息, key = instrument_id
     */
    private HashMap<String, FuturePosition4Fix> mFutureFixedPositionMap = new HashMap<>();

    /**
     * 当前全仓合约信息
     */
    private HashMap<String, FutureContractV3> mFutureCrossedContractMap = new HashMap<>();
    /**
     * 当前持有的全仓合约单信息, key = instrument_id
     */
    private HashMap<String, FuturePosition> mFutureCrossedPositionMap = new HashMap<>();

    private boolean mNeedUpdateInfo = false;

    public void putFixedAccountInfo(@NonNull Map<String, FutureContract4FixV3> contractMap,
                                    @NonNull List<FuturePosition4Fix> futureHoldList) {
        if (contractMap == null || futureHoldList == null) {
            SLogUtil.d(TAG, "putAccountInfo params are not null.");
            return;
        }
        mFutureFixedContractMap.clear();
        futureHoldList.clear();
        mFutureFixedContractMap.putAll(contractMap);
        for (FuturePosition4Fix hold : futureHoldList) {
            mFutureFixedPositionMap.put(hold.instrument_id, hold);
        }
        setNeedUpdateInfo(false);
        CallbackManager.get().onAccountInfoUpdated();
    }

    public void putCrossedAccountInfo(@NonNull Map<String, FutureContractV3> contractMap,
                                      @NonNull List<FuturePosition> futureHoldList) {
        if (contractMap == null || futureHoldList == null) {
            SLogUtil.d(TAG, "putAccountInfo params are not null.");
            return;
        }
        mFutureCrossedContractMap.clear();
        futureHoldList.clear();
        mFutureCrossedContractMap.putAll(contractMap);
        for (FuturePosition hold : futureHoldList) {
            mFutureCrossedPositionMap.put(hold.instrument_id, hold);
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
