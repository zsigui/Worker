package sg.jackiez.worker.module.ok.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import sg.jackiez.worker.module.ok.model.base.BaseM;
import sg.jackiez.worker.utils.DateUtil;

/**
 * 全仓持仓信息
 */
public class FuturePosition extends BaseM {

    /**
     * 	账户类型：全仓 crossed
     */
    public String margin_mode;

    /**
     * 	预估爆仓价
     */
    public double liquidation_price;

    /**
     * 	多仓数量
     */
    public long long_qty;

    /**
     * 多仓可平仓数量
     */
    public long long_avail_qty;

    /**
     * 开仓平均价
     */
    public double long_avg_cost;

    /**
     * 多仓结算基准价
     */
    public double long_settlement_price;

    /**
     * 已实现盈余
     */
    public double realized_pnl;

    /**
     * 	空仓数量
     */
    public long short_qty;

    /**
     * 空仓可平仓数量
     */
    public long short_avail_qty;

    /**
     * 	开仓平均价
     */
    public double short_avg_cost;

    /**
     * 	空仓结算基准价
     */
    public double short_settlement_price;

    /**
     * 	合约ID，如BTC-USD-180213
     */
    public String instrument_id;

    /**
     * 杠杆倍数
     */
    public int leverage;

    /**
     * 	创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
    public Date created_at;

    /**
     * 	更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
    public Date updated_at;

}
