package sg.jackiez.worker.utils.algorithm;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.annotations.NonNull;
import sg.jackiez.worker.utils.common.CommonUtil;

/**
 * @Author JackieZ
 * @Date Created on 2018/9/17
 */
public class RSI {

    private static final String TAG = "RSI";

    private int mPeriod1;
    private int mPeriod2;
    private int mPeriod3;

    public RSI() {
        this(6, 12, 24);
    }

    public RSI(int period1, int period2, int period3) {
        mPeriod1 = period1;
        mPeriod2 = period2;
        mPeriod3 = period3;
    }

    private List<Double> calculateRSI(@NonNull List<KlineInfo> klineList, int n) {
        double preLoss, preGain, dif, rs;
        int count = klineList.size();
        ArrayList<Double> rsList = new ArrayList<>(count);

        // 初始获取preLoss和PreGain
        KlineInfo prev = klineList.get(0), cur;
        int today = 0;
        preLoss = 0;
        preGain = 0;
        for (int i = n; i > 0; i--) {
            cur = klineList.get(today++);
            dif = cur.close - prev.close;
            prev = cur;
            if (dif > 0) {
                preGain += dif;
            } else {
                preLoss -= dif;
            }
        }
        preLoss /= n;
        preGain /= n;

        // 开始一项项的赋值
        today = 0;
        prev = klineList.get(0);
        double tmp;
        while (today < count) {
            cur = klineList.get(today++);
            dif = cur.close - prev.close;
            prev = cur;
            preLoss *= (n - 1);
            preGain *= (n - 1);
            if (dif > 0) {
                preGain += dif;
            } else {
                preLoss -= dif;
            }
            preGain /= n;
            preLoss /= n;
            tmp = preGain + preLoss;
            if (!(((-0.00000001) < tmp) && (tmp < 0.00000001))) {
                rs = 100 * preGain / tmp;
            } else {
                rs = 0;
            }
            rsList.add(rs);
        }

        return rsList;
    }

    public List<List<Double>> calculateRSI(List<KlineInfo> klineList) {
        if (CommonUtil.isEmpty(klineList)) {
            SLogUtil.v(TAG, "empty kline data to calculate macd value.");
            return null;
        }
        List<List<Double>> result = new ArrayList<>();
        result.add(calculateRSI(klineList, mPeriod1));
        result.add(calculateRSI(klineList, mPeriod2));
        result.add(calculateRSI(klineList, mPeriod3));
        return result;
    }
}
