package sg.jackiez.worker.utils.algorithm;

import java.util.List;

import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class CustomSharp {


    public static final int DIRECTION_AVG = 0;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;

    public int judgeEosDirection(List<KlineInfo> klineInfos,
                                  double newestPrice) {

        if (klineInfos == null || klineInfos.size() < 5) {
            return DIRECTION_AVG;
        }

        KlineInfo klineInfo = null;
        int upCount = 0;
        int downCount = 0;
        double totalVol = 0;    // 总的涨跌幅度
        double vol;

        int direction = DIRECTION_AVG;

        for (int start = 4 ; start >= 0; start--) {
            // 判断最近5条线的方向
            klineInfo = klineInfos.get(start);

            if (klineInfo == null) {
                return direction;
            }

            vol = klineInfo.close / klineInfo.open - 1;
            totalVol += vol;
            if (vol > 0) {
                // 上涨方向
                upCount ++;
            } else if (vol < 0) {
                // 下跌方向
                downCount ++;
            }
        }

        vol = newestPrice / klineInfo.close - 1;
        if (vol > 0) {
            upCount ++;
        } else if (vol < 0){
            downCount ++;
        }

        if (vol > 0.02 && vol < 0.04) {
            // 考虑最新瞬间暴涨的情况
            direction = DIRECTION_UP;
        } else if (vol < -0.02 && vol > -0.04) {
            // 考虑瞬间暴跌的情况
            direction = DIRECTION_DOWN;
        } else if (downCount > 3 && totalVol < -0.01 && vol < 0) {
            //  现在在下跌，且最近6次有4次下跌，且下跌总幅度达到10%
            direction = DIRECTION_DOWN;
        } else if (upCount > 3 && totalVol > +0.01 && vol > 0) {
            // 现在在上涨
            direction = DIRECTION_UP;
        }
        SLogUtil.d("test", "vol = " + vol + ", totalVol = " + totalVol + ", newestPrice = $" + newestPrice
        + ", klineInfo.close = $" + klineInfo.close + ", downCount = " + downCount + ", upCount = " + upCount);
        return direction;
    }

}
