package sg.jackiez.worker.debug;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class ProfitObj extends BaseM {

    /**
     * 下单金额
     */
    public double basePrice;

    /**
     * 卖出金额
     */
    public double closePrice;

    /**
     * 张数
     */
    public int amount;

    /**
     * 盈利率
     */
    public double profitRate;
}
