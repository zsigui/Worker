package sg.jackiez.worker.module.ok.model.resp;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class RespCancelTrade extends BaseM {

    // 针对单笔订单
    public boolean result;
    public long order_id;

    // 针对撤销多笔订单
    public String success;
    public String error;

}
