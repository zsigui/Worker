package sg.jackiez.worker.module.ok.model;

/**
 * 订单信息
 */
public class Order {

    /**
     * 委托数量
     */
    public double amount;
    /**
     * 平均成交价
     */
    public double avg_price;
    /**
     * 委托时间
     */
    public long create_date;
    /**
     * 成交数量
     */
    public double deal_amount;
    /**
     * 订单ID
     */
    public long order_id;
    /**
     * 订单ID(不建议使用)
     */
    public long orders_id;
    /**
     * 委托价格
     */
    public double price;
    /**
     * -1:已撤销  0:未成交  1:部分成交  2:完全成交 3:撤单处理中 4:下单失败 5:下单中
     */
    public int status;
    /**
     * 币对
     */
    public String symbol;
    /**
     * 买入/卖出类型
     */
    public String type;
}
