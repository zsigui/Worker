package sg.jackiez.worker.module.ok.model.resp;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class RespTrade extends BaseM {

    public boolean result;

    /**
     * 下单成功后返回的订单ID
     */
    public int order_id;

    /**
     * 错误码
     */
    public int errorCode;

}
