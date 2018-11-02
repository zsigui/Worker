package sg.jackiez.worker.module.ok.model.resp;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class RespCancelTradeV3 extends BaseM {

    // 针对单笔订单
    public boolean result;
    public long order_id;
    public String instrument_id;

}
