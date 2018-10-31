package sg.jackiez.worker.module.ok.model.account;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 全仓合约账户指定币种信息类
 */
public class FutureContractV3 extends BaseM {

    /**
     * 账户权益
     */
    public double equity;

    /**
     * 账户余额
     */
    public double total_avail_balance;

    /**
     * 	已用保证金
     */
    public double margin;

    /**
     * 账户类型：全仓 crossed
     */
    public double margin_mode;

    /**
     * 	保证金率
     */
    public double margin_ratio;

    /**
     * 已实现盈亏
     */
    public double realized_pnl;

    /**
     * 未实现盈亏
     */
    public double unrealized_pnl;
}
