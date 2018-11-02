package sg.jackiez.worker.module.ok.manager;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.model.InstrumentInfo;
import sg.jackiez.worker.module.ok.model.Leverage;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.module.ok.utils.ReqUtil;
import sg.jackiez.worker.utils.SLogUtil;

/**
 * 先导类，用于获取初始获取预设信息
 */
public class PrecursorManager {

    private static final String TAG = "PrecursorManager";

    private static PrecursorManager sInstance = new PrecursorManager();

    public static PrecursorManager get() {
        return sInstance;
    }

    private String mInstrumentId;
    private Leverage mLeverage;

    private PrecursorManager(){
    }

    public void init(String symbol, String contractType) {
        initInstrumentId(symbol, contractType);
        initAccountType(symbol, getInstrumentId());
    }

    public void initInstrumentId(String symbol, String contractType) {
        String resp = ReqUtil.retry(3, FutureRestApiV3::getInstruments);
        if (resp == null) {
            throw new RuntimeException("get no instrument data.");
        }

        List<InstrumentInfo> instrumentInfos = JsonUtil.jsonToSuccessDataForFuture(resp,
                new TypeReference<List<InstrumentInfo>>() {
                });
        if (instrumentInfos == null || instrumentInfos.isEmpty()) {
            throw new RuntimeException("translate json data error. src = " + resp);
        }

        String currency = symbol.replace("_", "-").toUpperCase();
        int startIndex = 0;
        final int count = matchToFindInstrumentId(contractType, instrumentInfos, currency, startIndex);
        if (count == 2) {
            startIndex = 1;
            matchToFindInstrumentId(contractType, instrumentInfos, currency, startIndex);
        } else if (count == 1) {
            startIndex = 2;
            matchToFindInstrumentId(contractType, instrumentInfos, currency, startIndex);
        }

        SLogUtil.i(TAG, "initInstrumentId : " + mInstrumentId);
    }

    private int matchToFindInstrumentId(String contractType, List<InstrumentInfo> instrumentInfos,
                                         String currency, int startIndex) {
        for (InstrumentInfo info : instrumentInfos) {
            if (info.instrument_id.startsWith(currency)) {
                startIndex++;
                if ((startIndex == 1 && contractType.equals(OKTypeConfig.CONTRACT_TYPE_THIS_WEEK))
                        || (startIndex == 2 && contractType.equals(OKTypeConfig.CONTRACT_TYPE_NEXT_WEEK))
                        || (startIndex == 3 && contractType.equals(OKTypeConfig.CONTRACT_TYPE_QUARTER))) {
                    mInstrumentId = info.instrument_id;
                    break;
                }
            }
        }
        return startIndex;
    }

    private void initAccountType(String sybmol, String instrumentId) {
        String resp = ReqUtil.retry(3, () -> FutureRestApiV3.getLeverage(sybmol.substring(0, sybmol.indexOf("_"))));
        if (resp == null) {
            throw new RuntimeException("get no leverage data.");
        }

        Leverage leverage = JsonUtil.jsonToLeverage(resp, instrumentId);
        if (leverage == null) {
            throw new RuntimeException("translate json data error. src = " + resp);
        }
        SLogUtil.i(TAG, "initAccountType : " + leverage);
        mLeverage = leverage;
    }

    public String getInstrumentId() {
        return mInstrumentId;
    }

    public int getLongLeverage() {
        return mLeverage.long_leverage;
    }

    public int getShortLeverage() {
        return mLeverage.short_leverage;
    }

    public boolean isFixedAccount() {
        return OKTypeConfig.ACCOUNT_TYPE_4_FIXED.equalsIgnoreCase(mLeverage.margin_mode);
    }
}
