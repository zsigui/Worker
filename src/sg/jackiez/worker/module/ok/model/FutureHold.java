package sg.jackiez.worker.module.ok.model;

/**
 * 合约账户持仓信息
 */
public class FutureHold {

    /**
     * 多仓数量
     */
    public int buy_amount;
    /**
     * 多仓可平仓数量
     */
    public int buy_available;
    /**
     * 开仓平均价
     */
    public double buy_price_avg;
    /**
     * 结算基准价
     */
    public double buy_price_cost;
    /**
     * 多仓已实现盈余
     */
    public double buy_profit_real;
    /**
     * 合约id
     */
    public long contract_id;
    /**
     * 创建日期
     */
    public long create_date;
    /**
     * 杠杆倍数
     */
    public int lever_rate;
    /**
     * 空仓数量
     */
    public int sell_amount;
    /**
     * 空仓可平仓数量
     */
    public int sell_available;
    /**
     * 开仓平均价
     */
    public double sell_price_avg;
    /**
     * 结算基准价
     */
    public double sell_price_cost;
    /**
     * 空仓已实现盈余
     */
    public double sell_profit_real;
    /**
     * 币对
     */
    public String symbol;
    /**
     * 合约类型
     */
    public String contract_type;
}
