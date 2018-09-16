package sg.jackiez.worker.module.util;

import java.util.HashMap;

import sg.jackiez.worker.callback.NoParamCallback;
import sg.jackiez.worker.callback.SimpleCallback;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.ThreadUtil;
import sg.jackiez.worker.utils.common.CommonUtil;
import sg.jackiez.worker.utils.http.HttpManager;

/**
 * @Author JackieZ
 * @Date Created on 2018/9/16
 */
public class UniversalDataSource {

	private static final class SingletonHolder {
		static final UniversalDataSource sInstance = new UniversalDataSource();
	}

	public static UniversalDataSource get() {
		return SingletonHolder.sInstance;
	}

	private static final String TAG = "UniversalDataSource";

	private static final String[] KEY_EXCHANGE = new String[]{
			"fx_scnyusd",
			"fx_susdcny"
	};

	private HashMap<String, Double> mChangeRateMap = new HashMap<>();

	private String spliceUrlAndRate() {
		StringBuilder builder = new StringBuilder("https://hq.sinajs.cn/rn=1537072279351list=");
		for (String key : KEY_EXCHANGE) {
			builder.append(key).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	public void refreshRateData(NoParamCallback callback) {
		ThreadUtil.post(() -> {
			String backData = HttpManager.get().doGet(spliceUrlAndRate());
			if (!CommonUtil.isEmpty(backData)) {
				String[] perRateAtLines = backData.split("\n");
				if (CommonUtil.isEmpty(perRateAtLines)) {
					SLogUtil.v(TAG, "init exchange rate data fail!");
					if (callback != null) {
						callback.onFail();
					}
					return;
				}

				String[] tmp;
				for (int i = perRateAtLines.length - 1; i >= 0; i--) {
					tmp = perRateAtLines[i].split(",");
					if (tmp.length < 3) {
						return;
					}

					mChangeRateMap.put(KEY_EXCHANGE[i], Double.parseDouble(tmp[2]));
				}

				if (callback != null) {
					callback.onSuccess();
				}

				SLogUtil.v(TAG, "init rate data : " + mChangeRateMap);
			} else {
				if (callback != null) {
					callback.onFail();
				}
				SLogUtil.v(TAG, "init exchange rate data fail for empty request data callback!");
			}
		});
	}

	public double getCnyToUsd() {
		Double rate = mChangeRateMap.get(KEY_EXCHANGE[0]);
		return rate == null ? 0 : rate;
	}

	public double getUsdToCny() {
		Double rate = mChangeRateMap.get(KEY_EXCHANGE[1]);
		return rate == null ? 0 : rate;
	}
}
