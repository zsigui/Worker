package sg.jackiez.worker.module.ok.model.resp;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import sg.jackiez.worker.module.ok.model.base.BaseM;
import sg.jackiez.worker.utils.DateUtil;

public class RespCancelTradeV3 extends BaseM {

    // 针对单笔订单
    public boolean result;
    public long order_id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
    public Date timestamp;

}
