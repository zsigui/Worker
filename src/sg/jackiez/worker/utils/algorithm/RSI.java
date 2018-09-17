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

	private int mPeriodN;

	public RSI() {
		// 默认使用六日周期指标，还有12/24的
		this(6);
	}

	public RSI(int periodN) {
		mPeriodN = periodN;
	}

	private List<Double> calculateRS(@NonNull List<KlineInfo> klineList, int n) {
		double upTotal, downTotal, dif, rs;
		int count = klineList.size();
		ArrayList<Double> rsList = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			upTotal = 0.0;
			downTotal = 0.0;
			// 通常统计数据应该是大于N，因为小于N部分未真正使用公式，实际不准确,可移除
			int firstInPeriod = i < n ? 0 : i - n;
			// 统计N日内收盘跌幅之和和涨幅之和
			for (int j = i - 1; j >= firstInPeriod; j--) {
				dif = klineList.get(j + 1).close - klineList.get(j).close;
				if (dif > 0) {
					upTotal += dif;
				} else {
					// 跌幅是负的，取正值所以用减
					downTotal -= dif;
				}
			}
			if (upTotal == 0 && downTotal == 0) {
				rs = 0;
			} else if (downTotal == 0) {
				rs = 100;
			} else {
				rs = upTotal / downTotal * 100;
			}
			rsList.add(rs);
		}
		return rsList;
	}

	public List<Double> calculateRSI(List<KlineInfo> klineList) {
		if (CommonUtil.isEmpty(klineList)) {
			SLogUtil.v(TAG, "empty kline data to calculate macd value.");
			return null;
		}

		List<Double> rsList = calculateRS(klineList, mPeriodN);

		int count = rsList.size();
		ArrayList<Double> rsiList = new ArrayList<>(count);
		double rsi;
		for (Double rs : rsList) {
			// RSI(N) = 100 * RS(N) / (1 + RS(N)) = 100 - 100 / (1 + RS(N))
			rsi = 100 * rs / (1 + rs);
			rsiList.add(rsi);
		}
		return rsiList;
	}
}
