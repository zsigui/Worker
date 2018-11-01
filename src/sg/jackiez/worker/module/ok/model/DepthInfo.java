package sg.jackiez.worker.module.ok.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

import sg.jackiez.worker.utils.DateUtil;

/**
 * 市场深度数据
 */
public class DepthInfo {

    /**
     * 卖方深度，[深度价格，此价格数量，此价格爆仓张数，此深度由几笔订单组成]
     */
    public List<List<Double>> asks;

    /**
     * 买方深度
     */
    public List<List<Double>> bids;

    /**
     * 深度数据生成时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
    public Date timestamp;

    @Override
    public String toString() {
        return "DepthInfo{" +
                "asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}
