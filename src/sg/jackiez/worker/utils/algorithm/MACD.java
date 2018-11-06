package sg.jackiez.worker.utils.algorithm;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
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
	private EMA mEMA;

	public MACD() {
		this(FAST_PERIOD, SLOW_PERIOD, DIF_PERIOD, true);
	}

	public MACD(int periodFast, int periodSlow, int periodDif, boolean isUseDI) {
		mPeriodFast = periodFast;
		mPeriodSlow = periodSlow;
		mPeriodDif = periodDif;
		mEMA = new EMA(isUseDI);
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

		List<Double> emaFastList = mEMA.calculateEMAs(klineList, mPeriodFast);
		List<Double> emaDifList = mEMA.calculateEMAs(klineList, mPeriodDif);
		List<Double> emaSlowList = mEMA.calculateEMAs(klineList, mPeriodSlow);

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
