package sg.jackiez.worker.module.ok.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.model.FuturePosition;
import sg.jackiez.worker.module.ok.model.FuturePosition4Fix;
import sg.jackiez.worker.module.ok.model.account.FutureContract4FixV3;
import sg.jackiez.worker.module.ok.model.account.FutureContractV3;
import sg.jackiez.worker.module.ok.utils.Utils;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.annotations.NonNull;
import sg.jackiez.worker.utils.common.CommonUtil;

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
     * 当前持有的全仓合约单信息, key = currency
     */
    private HashMap<String, FuturePosition> mFutureCrossedPositionMap = new HashMap<>();

    private boolean mNeedUpdateInfo = false;

    /**
     * 每次允许最大下单张数
     */
    private int mAllowedMaxCount = 100;
    /**
     * 每次允许最大下单金额占总金额的比例(0.0f ~ 1.0f)
     */
    private float mAllowedMaxPercent = 0.1f;

    private boolean mIsFixedAccount = true;

    public void setAllowedMaxCount(int allowedMaxCount) {
        mAllowedMaxCount = allowedMaxCount;
    }

    public void setAllowedMaxPercent(float allowedMaxPercent) {
        mAllowedMaxPercent = allowedMaxPercent;
    }

    public void setFixedAccount(boolean fixedAccount) {
        mIsFixedAccount = fixedAccount;
    }

    public boolean isFixedAccount() {
        return mIsFixedAccount;
    }

    /**
     * 获取对应币种当前持有的订单金额
     * @param currency 币种
     * @param pageValue 合约面值
     * @return
     */
    public int getTotalHoldingPageForCurrency(String currency, double priceInUsd, double pageValue) {
        if (CommonUtil.isEmpty(currency)) {
            return 0;
        }

        int result = 0;
        if (isFixedAccount()) {
            for (FuturePosition4Fix item : mFutureFixedPositionMap.values()) {
                if (item.instrument_id.startsWith(currency.toUpperCase())) {
                    result += item.long_margin + item.short_margin + item.realized_pnl;
                }
            }
        } else {
            FutureContractV3 item = mFutureCrossedContractMap.getOrDefault(currency, null);
            if (item != null) {
                result += item.margin + item.realized_pnl;
            }
        }
        return (int) (Math.min(0, result) * priceInUsd / pageValue);
    }

    /**
     * 获取当前可下单的张数
     * @param currency 币种
     * @param priceInUsd 对应价格
     * @param pageValue 对应的合约面值
     * @param feeRate 手续费
     * @param leverage 杠杆倍率
     * @return
     */
    public int getRemainAvailableCount(String currency, double priceInUsd, double pageValue,
                                       double feeRate, int leverage) {
        int resultPage;
        double totalAvailBalance = 0;
        if (isFixedAccount()) {
            FutureContract4FixV3 contract = mFutureFixedContractMap.getOrDefault(currency, null);
            if (contract != null) {
                totalAvailBalance = contract.total_avail_balance;
            }
        } else {
            FutureContractV3 contract = mFutureCrossedContractMap.getOrDefault(currency, null);
            if (contract != null) {
                totalAvailBalance = contract.total_avail_balance;
            }
        }
        resultPage = Utils.calculatePageAmount(totalAvailBalance, priceInUsd,
                pageValue, feeRate, leverage);
        resultPage = Math.min((int)(resultPage * mAllowedMaxPercent), mAllowedMaxCount);
        return Math.min(0, resultPage);
    }

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

    /**
     * 打印当前账户持仓信息
     */
    public void printLogInfo() {
        if (PrecursorManager.get().isFixedAccount()) {
            SLogUtil.i(TAG, "逐仓合约信息：");
            SLogUtil.i(TAG, mFutureFixedContractMap);
            SLogUtil.i(TAG, "逐仓持仓信息：");
            SLogUtil.i(TAG, mFutureFixedPositionMap);
        } else {
            SLogUtil.i(TAG, "全仓合约信息：");
            SLogUtil.i(TAG, mFutureCrossedContractMap);
            SLogUtil.i(TAG, "逐仓持仓信息：");
            SLogUtil.i(TAG, mFutureCrossedPositionMap);
        }
    }
}
