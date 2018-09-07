package sg.jackiez.worker.module.ok.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 订单信息
 */
public class Order {

    /**
     * 买入
     */
    public static final String TYPE_BUY = "buy";
    /**
     * 卖出
     */
    public static final String TYPE_SELL = "sell";
    /**
     * 市价买入
     */
    public static final String TYPE_BUY_MARKET = "buy_market";
    /**
     * 市价卖出
     */
    public static final String TYPE_SELL_MARKET = "sell_market";

    public double amount;
    @JsonProperty("avg_price")
    public double avgPrice;
    @JsonProperty("create_date")
    public long createDate;
    @JsonProperty("deal_amount")
    public double dealAmount;
    @JsonProperty("order_id")
    public int orderId;
    @JsonProperty("orders_id")
    public int ordersId;
    public double price;
    public int status;
    public String type;
}
