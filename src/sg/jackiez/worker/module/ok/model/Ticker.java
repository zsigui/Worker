package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class Ticker extends BaseM {

    /**
     * 合约ID，仅当进行期货请求时返回
     */
    public long contract_id;
    /**
     * 合约面值
     */
    public double unit_amount;
    /**
     * 买一价
     */
    public double buy;
    /**
     * 最高价
     */
    public double high;
    /**
     * 最新成交价
     */
    public double last;
    /**
     * 最低价
     */
    public double low;
    /**
     * 卖一价
     */
    public double sell;
    /**
     * 成交量（最近24小时，对于合约代表张数）
     */
    public double vol;
    /**
     * 24小时最高价
     */
    public double day_high;
    /**
     * 24小时最低价
     */
    public double day_low;
    /**
     * 币的成交量
     */
    public double coin_vol;
}
