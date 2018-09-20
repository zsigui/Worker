package sg.jackiez.worker.module.ok.model.resp;

import java.util.List;

import sg.jackiez.worker.module.ok.model.FutureHold;
import sg.jackiez.worker.module.ok.model.base.BaseM;

public class RespFuturePosition extends BaseM {
    /**
     * 预估爆仓价
     */
    public double force_liqu_price;
    public List<FutureHold> holding;

    public boolean result;
}
