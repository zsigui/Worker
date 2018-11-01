package sg.jackiez.worker.module.ok.performance;

import java.util.List;

import sg.jackiez.worker.module.ok.model.DepthInfo;

public interface IPerformance {

    void init();

    void beforeTrade();

    void handleBar(List<DepthInfo> depthData);

    void afterTrade();
}
