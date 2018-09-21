package sg.jackiez.worker.module.ok.model.resp;

import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.utils.DateUtil;

public class RespTicker{

    public int date;

    public Ticker ticker;

    @Override
    public String toString() {
        return "{" +
                "'date'='" + DateUtil.formatUnixTime(date * 1000) + '\'' +
                ", 'ticker'='" + ticker + '\'' +
                '}';
    }
}
