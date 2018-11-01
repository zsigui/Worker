package sg.jackiez.worker.module.ok.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import sg.jackiez.worker.module.ok.model.base.BaseM;
import sg.jackiez.worker.utils.DateUtil;

public class FutureOrder extends BaseM {

    /**
     * 合约ID，如BTC-USD-180213
     */
    public String instrument_id;

    /**
     * 数量
     */
    public int size;

    /**
     * 委托时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
    public Date timestamp;
    /**
     * 成交数量
     */
    public int filled_qty;
    /**
     * 手续费
     */
    public double fee;
    /**
     * 订单ID
     */
    public String order_id;
    /**
     * 订单价格
     */
    public double price;
    /**
     * 平均价格
     */
    public double price_avg;
    /**
     * 订单状态(-1.撤单成功；0:等待成交 1:部分成交 2:已完成）
     */
    public int status;
    /**
     * 订单类型(1:开多 2:开空 3:开多 4:平空)
     */
    public int type;
    /**
     * 合约面值
     */
    public double contract_val;
    /**
     * 杠杆倍数 value:10/20 默认10
     */
    public String leverage;

}
