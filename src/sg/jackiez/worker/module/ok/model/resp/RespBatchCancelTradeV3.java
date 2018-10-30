package sg.jackiez.worker.module.ok.model.resp;

import java.util.List;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class RespBatchCancelTradeV3 extends BaseM {

    public boolean result;

    public List<String> order_ids;

    public String instrument_id;

}
