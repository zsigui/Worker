package sg.jackiez.worker.module.ok.callback;

import java.util.List;

import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public interface FutureDataChangedCallback {

    void onDepthUpdated(List<DepthInfo> depthInfoList);

    void onKlineInfoUpdated(List<KlineInfo> klineInfoList);
}
