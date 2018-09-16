package sg.jackiez.worker.utils.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.annotations.NonNull;
import sg.jackiez.worker.utils.common.CommonUtil;

/**
 * @Author JackieZ
 * @Date Created on 2018/9/16
 */
public class KDJ {

	private static final String TAG = "KDJ";

	private static final int RSV_PERIOD = 9;

	private List<Double> calculateRSV(@NonNull List<KlineInfo> klineList, int n) {

		double lowest, highest, close, rs;
		final int totalCount = klineList.size();
		KlineInfo tmp;
		ArrayList<Double> rsv = new ArrayList<>(klineList.size());
		for (int i = 0; i < totalCount; i++) {
			// 重设值
			highest = -1;
			lowest = -1;
			close = -1;

			// 数据量小于周期N，只找全部里面最低和最高价，如果数据量大于N，则找周期N内数据量即可
			int count = (totalCount - i) < n ? (totalCount - i) : n;
			for (int j = 0; j < count; j++) {
				tmp = klineList.get(i + j);
				highest = (highest > tmp.highest ? highest : tmp.highest);
				lowest = (lowest < tmp.lowest ? lowest : tmp.lowest);
			}

			// RS(N) = (CN - LN) / (HN - LN) * 100
			// 当 HN = LN, 则 RS(N) = 0
			if (lowest != highest) {
				rs = (close - lowest) / (highest - lowest) * 100;
			} else {
				rs = 0.0;
			}
			rsv.add(rs);
		}
		return rsv;
	}

	/**
	 * 根据市场盘面数据计算KDJ指标数据
	 *
	 * @param klineList
	 * @return 参数异常及计算失败返回null，成功返回[K数据，D数据，J数据]
	 */
	public List<List<Double>> calculateKDJ(List<KlineInfo> klineList) {
		if (CommonUtil.isEmpty(klineList)) {
			SLogUtil.v(TAG, "empty kline data to calculate macd value.");
			return null;
		}

		final int count = klineList.size();
		ArrayList<Double> kList = new ArrayList<>(count);
		ArrayList<Double> dList = new ArrayList<>(count);
		ArrayList<Double> jList = new ArrayList<>(count);

		double kv = 50, dv = 50, jv;
		List<Double> rsvList = calculateRSV(klineList, RSV_PERIOD);
		for (int i = 0; i < count; i++) {
			kv = (kv * 2 + rsvList.get(i)) / 3;
			dv = (dv * 2 + kv) / 3;
			jv = 3 * kv - 2 * dv;
			kList.add(kv);
			dList.add(dv);
			jList.add(jv);
		}

		List<List<Double>> result = new ArrayList<>(3);
		result.add(kList);
		result.add(dList);
		result.add(jList);
		return result;
	}
}
