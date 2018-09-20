package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class FutureHoldAmount extends BaseM {

    /**
     * 合约名，如BTC0213
     */
    public String contract_name;
    /**
     * 总持仓量（张）
     */
    public int amount;
}
