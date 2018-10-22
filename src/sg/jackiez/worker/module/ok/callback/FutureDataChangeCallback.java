package sg.jackiez.worker.module.ok.callback;

import java.util.List;

import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public interface FutureDataChangeCallback {

    void onDepthUpdated(DepthInfo depthInfo);

    void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos,
                            String longTimeType, List<KlineInfo> longKlineInfos);

    void onTickerDataUpdate(Ticker ticker);

    void onGetTradeHistory(List<TradeHistoryItem> tradeHistory);
}
