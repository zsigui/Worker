package sg.jackiez.worker.module.ok.model;

public class Ticker {

    /**
     * 合约ID，仅当进行期货请求时返回
     */
    public int contract_id;
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
     * 成交量（最近24小时）
     */
    public double vol;

    @Override
    public String toString() {
        return "Ticker{" +
                "buy=" + buy +
                ", high=" + high +
                ", last=" + last +
                ", low=" + low +
                ", sell=" + sell +
                ", vol=" + vol +
                '}';
    }
}
