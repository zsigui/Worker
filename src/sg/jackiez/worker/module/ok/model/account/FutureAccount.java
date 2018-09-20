package sg.jackiez.worker.module.ok.model.account;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 合约账户信息
 */
public class FutureAccount extends BaseM {

    /**
     * 账户权益
     */
    public int account_rights;
    /**
     * 保证金
     */
    public double keep_deposit;
    /**
     * 已实现盈亏
     */
    public double profit_real;
    /**
     * 未实现盈亏
     */
    public double profit_unreal;
    /**
     * 保证金率
     */
    public double risk_rate;
}
