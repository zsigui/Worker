package sg.jackiez.worker.module.ok.callback;

import java.util.List;

import io.netty.util.internal.ConcurrentSet;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class CallbackManager implements FutureDataChangedCallback{

    private static final class SingletonHolder {
        static final CallbackManager sInstance = new CallbackManager();
    }

    public static CallbackManager get() {
        return SingletonHolder.sInstance;
    }

    private ConcurrentSet<FutureDataChangedCallback> mFutureDataChangedCallbacks = new ConcurrentSet<>();

    public void addFutureDataChangeCallback(FutureDataChangedCallback callback) {
        if (callback == null) {
            return;
        }
        mFutureDataChangedCallbacks.add(callback);
    }

    public void removeFutureDataChangeCallback(FutureDataChangedCallback callback) {
        if (callback == null) {
            return;
        }
        mFutureDataChangedCallbacks.remove(callback);
    }


    @Override
    public void onDepthUpdated(List<DepthInfo> depthInfoList) {
        for (FutureDataChangedCallback callback: mFutureDataChangedCallbacks) {
            callback.onDepthUpdated(depthInfoList);
        }
    }

    @Override
    public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos,
                                   String longTimeType, List<KlineInfo> longKlineInfos) {
        for (FutureDataChangedCallback callback: mFutureDataChangedCallbacks) {
            callback.onKlineInfoUpdated(shortTimeType, shortKlineInfos, longTimeType, longKlineInfos);
        }
    }

    @Override
    public void onTickerDataUpdate(Ticker ticker) {
        for (FutureDataChangedCallback callback: mFutureDataChangedCallbacks) {
            callback.onTickerDataUpdate(ticker);
        }
    }
}
