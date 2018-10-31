package sg.jackiez.worker.module.ok.model;

/**
 * 逐仓持仓信息
 */
public class FuturePosition4Fix extends FuturePosition{

    /**
     * 多仓保证金
     */
    public double long_margin;

    /**
     * 多仓强平价格
     */
    public double long_liqui_price;

    /**
     * 多仓收益率
     */
    public double long_pnl_ratio;

    /**
     * 多仓杠杆倍数
     */
    public double long_leverage;

    /**
     * 	空仓保证金
     */
    public double short_margin;

    /**
     * 	空仓强平价格
     */
    public double short_liqui_price;

    /**
     * 空仓收益率
     */
    public double short_pnl_ratio;

    /**
     * 空仓杠杆倍数
     */
    public double short_leverage;
}
