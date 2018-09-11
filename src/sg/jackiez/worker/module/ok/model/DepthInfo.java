package sg.jackiez.worker.module.ok.model;

import java.util.List;

/**
 * 市场深度数据
 */
public class DepthInfo {

    /**
     * 卖方深度
     */
    public List<List<Double>> asks;

    /**
     * 买方深度
     */
    public List<List<Double>> bids;

    @Override
    public String toString() {
        return "DepthInfo{" +
                "asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}
