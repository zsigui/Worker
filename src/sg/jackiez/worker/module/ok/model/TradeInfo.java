package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.utils.DateUtil;

public class TradeInfo {

    /**
     * 交易时间(s)
     */
    public int date;
    /**
     * 交易时间(ms)
     */
    public long date_ms;
    /**
     * 交易价格
     */
    public double price;
    /**
     * 交易数量
     */
    public double amount;
    /**
     * 交易生成ID
     */
    public String tid;
    /**
     * 交易类型，如buy/sell
     */
    public String type;

    @Override
    public String toString() {
        return "TradeInfo{" +
                "date=" + DateUtil.formatUnixTime(date_ms) +
                ", date_ms=" + date_ms +
                ", price=" + price +
                ", amount=" + amount +
                ", tid='" + tid + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
