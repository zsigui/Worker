package sg.jackiez.worker.utils.algorithm;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.annotations.NonNull;

/**
 * 平均移动指数
 */
public class EMA {

    // 指示是否使用需求指数进行计算，否则使用收盘价
    private boolean mIsUseDI;

    public EMA() {
        this(true);
    }

    public EMA(boolean isUseDI) {
        mIsUseDI = isUseDI;
    }

    /**
     * 根据当日盘面数据获取需求指数
     */
    private double getDemandIndex(KlineInfo kline) {
        // 需求指数，比起单独用收盘价来计算每日移动平均值更好点
        return mIsUseDI ? (kline.close * 2 + kline.highest + kline.lowest) / 4 : kline.close;
    }


    public List<Double> calculateEMAs(@NonNull List<KlineInfo> klineList, int n) {
        List<Double> emaList = new ArrayList<>();
        double ema;
        KlineInfo kline;
        final int count = klineList.size();
        for (int i = 0; i < count; i++) {
            kline = klineList.get(i);
            if (i == 0) {
                // 初始默认取值当日需求指数
                ema = getDemandIndex(kline);
            } else {
                // 平滑系数 = 2 / (n + 1)
                // EMA(N) = (今日需求指数 - EMA(N - 1)) * 平滑系数 + EMA(N - 1)
                ema = (getDemandIndex(kline) - emaList.get(i - 1)) * 2 / (n + 1) + emaList.get(i - 1);
            }
            emaList.add(ema);
        }
        return emaList;
    }
}
