package sg.jackiez.worker.module.ok.model.resp;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 合约最高限价返回
 */
public class RespFuturePriceLimit extends BaseM {

    /**
     * 最高买价
     */
    public double high;
    /**
     * 最高卖价
     */
    public double low;
}
