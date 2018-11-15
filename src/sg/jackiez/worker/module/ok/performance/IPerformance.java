package sg.jackiez.worker.module.ok.performance;

import sg.jackiez.worker.module.ok.model.TradeHistoryItem;

public interface IPerformance {

    void init();

    void handleBar(TradeHistoryItem newestTradeItem);

    void beforeTrade();

    void doTrade();

    void afterTrade();
}
