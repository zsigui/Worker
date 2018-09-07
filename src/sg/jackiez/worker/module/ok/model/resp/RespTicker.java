package sg.jackiez.worker.module.ok.model.resp;

import sg.jackiez.worker.module.ok.model.Ticker;

public class RespTicker{

    public int date;

    public Ticker ticker;

    @Override
    public String toString() {
        return "RespTicker{" +
                "date='" + date + '\'' +
                ", ticker=" + ticker +
                '}';
    }
}
