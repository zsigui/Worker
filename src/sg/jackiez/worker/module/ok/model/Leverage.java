package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 杠杆信息
 */
public class Leverage extends BaseM {

    /**
     * 全仓表示币种 或者 多仓表示合约ID
     */
    public String key;

    /**
     * 多仓杠杆倍数，全仓时与short_leverage一致
     */
    public int long_leverage;

    /**
     * 空仓杠杠倍数
     */
    public int short_leverage;

    /**
     * 账户类型
     */
    public String margin_mode;
}
