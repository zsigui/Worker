package sg.jackiez.worker.utils.algorithm;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.annotations.NonNull;
import sg.jackiez.worker.utils.common.CommonUtil;

/**
 * 经过个别参数变更的MACD指数计算类
 *
 * @Author JackieZ
 * @Date Created on 2018/9/16
 */
public class MACD {

	private static final String TAG = "MACD";

	// 常用快移动周期值是12
	private static final int FAST_PERIOD = 12;
	// 常用慢移动周期值是26
	private static final int SLOW_PERIOD = 26;
	// 常用DIF周期值是9
	private static final int DIF_PERIOD = 9;

	private int mPeriodFast;
	private int mPeriodSlow;
	private int mPeriodDif;
	// 指示是否使用需求指数进行计算，否则使用收盘价
	private boolean mIsUseDI;

	public MACD() {
		this(FAST_PERIOD, SLOW_PERIOD, DIF_PERIOD, true);
	}

	public MACD(int periodFast, int periodSlow, int periodDif, boolean isUseDI) {
		mPeriodFast = periodFast;
		mPeriodSlow = periodSlow;
		mPeriodDif = periodDif;
		mIsUseDI = isUseDI;
	}

	/**
	 * 根据当日盘面数据获取需求指数
	 */
	private double getDemandIndex(KlineInfo kline) {
		// 需求指数，比起单独用收盘价来计算每日移动平均值更好点
		return mIsUseDI ? (kline.close * 2 + kline.highest + kline.lowest) / 4 : kline.close;
	}

	private List<Double> calculateEMAs(@NonNull  List<KlineInfo> klineList, int n) {
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

	/**
	 * 根据传入的数据计算Macd值
	 * @param klineList
	 * @return 传入参数异常或者执行异常返回null，另外返回[DIF，DEA, BAR]
	 */
	public List<List<Double>> calculateMACD(List<KlineInfo> klineList) {
		if (CommonUtil.isEmpty(klineList)) {
			SLogUtil.v(TAG, "empty kline data to calculate macd value.");
			return null;
		}

		List<Double> emaFastList = calculateEMAs(klineList, mPeriodFast);
		List<Double> emaDifList = calculateEMAs(klineList, mPeriodDif);
		List<Double> emaSlowList = calculateEMAs(klineList, mPeriodSlow);

		if (emaFastList == null || emaDifList == null || emaSlowList == null) {
			SLogUtil.v(TAG, "error happen on calculate emaFast or emaSlow or emaDif.");
			return null;
		}

		final int count = klineList.size();
		ArrayList<Double> difList = new ArrayList<>(count);
		ArrayList<Double> deaList = new ArrayList<>(count);
		ArrayList<Double> barList = new ArrayList<>(count);

		// 起始dea算0
		double dif, dea = 0, bar;
		for (int i = 0; i < count; i++) {
			// DIF(N) = F_EMA(N) - S_EMA(N)
			dif = emaFastList.get(i) - emaSlowList.get(i);
			// DEA(N) = DEA(N-1) * 0.8 + DIF(N) * 0.2
			dea = dea * 0.8 + dif * 0.2;
			// BAR(N) = (DIF(N) - DEA(N)) * 2
			bar = 2 * (dif - dea);

			difList.add(dif);
			deaList.add(dea);
			barList.add(bar);
		}

		List<List<Double>> result = new ArrayList<>(3);
		result.add(difList);
		result.add(deaList);
		result.add(barList);
		return result;
	}
}
