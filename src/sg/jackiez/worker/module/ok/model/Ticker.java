package sg.jackiez.worker.module.ok.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import sg.jackiez.worker.module.ok.model.base.BaseM;
import sg.jackiez.worker.utils.DateUtil;

public class Ticker extends BaseM {

    /**
     * 合约ID
     */
    public String instrumentId;

    /**
     * 	最新成交价
     */
    public double last;

    /**
     * 卖一价
     */
    public double best_ask;

    /**
     * 买一价
     */
    public double best_bid;

    /**
     * 24小时最高价
     */
    public double high_24h;

    /**
     * 24小时最低价
     */
    public double low_24h;

    /**
     * 24小时成交量，按张数统计
     */
    public long volume_24h;

    /**
     * 系统时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
    public Date timestamp;


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ticker)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Ticker t = (Ticker) obj;
        return best_ask == t.best_ask && last == t.last
                && best_bid == t.best_bid;
    }
}
