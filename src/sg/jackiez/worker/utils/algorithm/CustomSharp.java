package sg.jackiez.worker.utils.algorithm;

import java.util.List;

import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class CustomSharp {


    public static final int DIRECTION_AVG = 0;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;

    public void judgeEosDirection(List<KlineInfo> _1minKlineInfos,
                                  List<KlineInfo> _15minKlineInfos,
                                  double newestPrice) {
        KlineInfo klineInfo = null;
        int upCount = 0;
        int downCount = 0;
        double totalVol = 0;    // 总的涨跌幅度
        double vol;

        int possible = 0;
        int direction = DIRECTION_AVG;

        for (int i = 5; i >= 0; i--) {
            // 判断最近5分钟方向
            klineInfo = _1minKlineInfos.get(i);
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

        if (downCount > 4 && totalVol < 0.015) {
            //  向下跌
        } else if (upCount > 4 && totalVol > 0.015) {
            // 向上涨
        }
    }

}
