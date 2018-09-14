package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.utils.DateUtil;

public class KlineInfo {

    /**
     * 时间戳
     */
    public long time;

    /**
     * 开盘价
     */
    public double open;

    /**
     * 最高价
     */
    public double highest;

    /**
     * 最低价
     */
    public double lowest;

    /**
     * 收盘价
     */
    public double close;

    /**
     * 交易量
     */
    public double volumn;

    @Override
    public String toString() {
        return "{" +
                "time=" + DateUtil.formatUnixTime(time) +
                ", open=" + open +
                ", highest=" + highest +
                ", lowest=" + lowest +
                ", volumn=" + volumn +
                '}';
    }
}
