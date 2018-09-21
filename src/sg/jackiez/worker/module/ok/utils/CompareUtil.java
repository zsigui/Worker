package sg.jackiez.worker.module.ok.utils;

import sg.jackiez.worker.module.ok.model.Ticker;

public class CompareUtil {

    public static boolean equal(Ticker t1, Ticker t2) {
        if (t1 == t2) {
            return true;
        }
        if (t1 == null || t2 == null) {
            return false;
        }
        return t1.buy == t2.buy && t1.last == t2.last
                && t1.sell == t2.sell && t1.high == t2.high
                && t1.vol == t2.vol;
    }
}
