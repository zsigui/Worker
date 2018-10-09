package sg.jackiez.worker.utils.algorithm;

import java.util.List;

import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

/**
 * 瞬间涨落计算
 */
public class PIZ {

    public double calculate(List<KlineInfo> klineInfosFor1min) {
        KlineInfo first;
        KlineInfo second;

        double lastRate = klineInfosFor1min.get(0).close / klineInfosFor1min.get(1).close;
        if (lastRate > 0.01d || lastRate < -0.01d) {
            // 突然暴涨暴跌1%，强烈做多或者做空信号
            return lastRate > 0 ? 1 : -11;
        } else {
            double finalRate = 0;
            int upCount = 0;
            int downCount = 0;
            int size = klineInfosFor1min.size() > 5 ? 5 : klineInfosFor1min.size();
            for (int i = 1; i < size; i++) {
                first = klineInfosFor1min.get(i);
                second = klineInfosFor1min.get(i - 1);
                finalRate += (second.close / first.close - 1);
                if (second.close > first.close) {
                    upCount++;
                } else if (second.close < first.close) {
                    downCount++;
                }
            }
            if ((upCount > 3 && finalRate > 0.005d)
                    || (downCount > 3 && finalRate < -0.005d)) {
                // 连续三次上涨/下跌且最终幅度超过0.5%，较强做多做空信号
                return finalRate > 0 ? 0.5 : -0.5;
            } else {
                // 跌宕，当前信号情况不适合买入
                return 0;
            }
        }

    }
}
