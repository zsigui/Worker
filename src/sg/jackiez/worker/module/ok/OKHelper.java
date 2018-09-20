package sg.jackiez.worker.module.ok;

import java.util.HashMap;
import java.util.List;

import sg.jackiez.worker.module.ok.model.ErrorItem;
import sg.jackiez.worker.module.ok.model.PairInfo;
import sg.jackiez.worker.utils.FileUtil;
import sg.jackiez.worker.utils.ModelUtil;
import sg.jackiez.worker.utils.SLogUtil;

/**
 * 进行OK交易的特殊处理帮助类
 */
public class OKHelper {

    private static final String TAG = "OKHelper";

    private HashMap<Integer, ErrorItem> mSpotErrMap;
    private HashMap<Integer, ErrorItem> mFutureErrMap;
    private HashMap<String, PairInfo> mPairMap;

    private static OKHelper sInstance;

    public static OKHelper get() {
        if (sInstance == null) {
            synchronized (OKHelper.class) {
                if (sInstance == null) {
                    sInstance = new OKHelper();
                }
            }
        }
        return sInstance;
    }

    private OKHelper() {
        init();
    }

    private void init() {
        if (mSpotErrMap == null) {
            List<ErrorItem> items = ModelUtil.readModelFromCustomFile(FileUtil.getFileBaseCurrentWork(OkConfig.FILE_SPOT_ERROR),
                    ErrorItem.class);
            if (items != null) {
                mSpotErrMap = new HashMap<>(items.size());
                for (ErrorItem item : items) {
                    mSpotErrMap.put(item.code, item);
                }
            }
        }
        if (mFutureErrMap == null) {
            List<ErrorItem> items = ModelUtil.readModelFromCustomFile(FileUtil.getFileBaseCurrentWork(OkConfig.FILE_FUTURE_ERROR),
                    ErrorItem.class);
            if (items != null) {
                mFutureErrMap = new HashMap<>(items.size());
                for (ErrorItem item : items) {
                    mFutureErrMap.put(item.code, item);
                }
            }
        }
        if (mPairMap == null) {
            List<PairInfo> items = ModelUtil.readModelFromCustomFile(FileUtil.getFileBaseCurrentWork(OkConfig.FILE_PAIRS_INCREMENT),
                    PairInfo.class);
            if (items != null) {
                mPairMap = new HashMap<>(items.size());
                for (PairInfo item : items) {
                    mPairMap.put(item.symbol, item);
                }
            }
        }
    }

    public ErrorItem findErrorItemForSpot(int code) {
        if (mSpotErrMap == null) {
            return null;
        }
        return mSpotErrMap.get(code);
    }

    public ErrorItem findErrorItemForFuture(int code) {
        if (mFutureErrMap == null) {
            return null;
        }
        return mFutureErrMap.get(code);
    }

    public PairInfo findPairInfo(String symbol) {
        if (mPairMap == null) {
            return null;
        }
        return mPairMap.get(symbol);
    }

    public void print() {
        SLogUtil.v(TAG, mPairMap);
        SLogUtil.v(TAG, mSpotErrMap);
        SLogUtil.v(TAG, mFutureErrMap);
    }
}
