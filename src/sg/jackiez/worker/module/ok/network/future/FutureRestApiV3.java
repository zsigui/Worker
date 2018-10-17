package sg.jackiez.worker.module.ok.network.future;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.annotations.NonNull;
import sg.jackiez.worker.utils.chiper.HmacSHA256;
import sg.jackiez.worker.utils.common.CollectionUtil;
import sg.jackiez.worker.utils.common.CommonUtil;
import sg.jackiez.worker.utils.http.HttpManager;
import sg.jackiez.worker.utils.http.HttpUtil;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/16
 */
public class FutureRestApiV3 {

	private static final String TAG = "FutureRestApiV3";

	private static String preSignData(String timestamp, String method, String requestPath, String body) {
		StringBuilder preHash = new StringBuilder();
		preHash.append(timestamp);
		preHash.append(method.toUpperCase());
		preHash.append(requestPath);
		if (!CommonUtil.isEmpty(body)) {
			preHash.append(body);
		}
		return preHash.toString();
	}

	private String doGet(String requestUrl, Map<String, String> params) {
		String paramStr = HttpUtil.convertMapToEncodedUrlParam(params);
		if (!CommonUtil.isEmpty(paramStr)) {
			requestUrl = HttpUtil.spliceUrlAndParam(requestUrl, paramStr);
		}
		String timestamp = DateUtil.formatISOTime(System.currentTimeMillis());
		String data = preSignData(timestamp, "GET",
				requestUrl.replace(OkConfig.REST_HOST, ""), null);
		String sign = Base64.getEncoder().encodeToString(
				HmacSHA256.genDigest(data, OkConfig.V3_SECRET_KEY));
		HashMap<String, String> headers = CollectionUtil.getExtraMap(
				OkConfig.HEADER_ACCESS_KEY, OkConfig.V3_API_KEY,
				OkConfig.HEADER_ACCESS_SIGN, sign,
				OkConfig.HEADER_ACCESS_PASSPHRASE, OkConfig.V3_PASSPHRASE,
				OkConfig.HEADER_ACCESS_TIMESTAMP, timestamp
		);
		SLogUtil.d(TAG, "doGet: data = " + data + "\nsign = " + sign +"\nheader = " + headers);
		return HttpManager.get().doGet(requestUrl, null, headers);
	}

	private String doJsonPost(String requestUrl, Map<String, String> params, String jsonData) {
		String paramStr = HttpUtil.convertMapToEncodedUrlParam(params);
		if (!CommonUtil.isEmpty(paramStr)) {
			requestUrl = HttpUtil.spliceUrlAndParam(requestUrl, paramStr);
		}

		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String data = preSignData(timestamp, "POST",
				requestUrl.replace(OkConfig.REST_HOST, ""), jsonData);
		String sign = Base64.getEncoder().encodeToString(
				HmacSHA256.genDigest(data, OkConfig.V3_PASSPHRASE));
		HashMap<String, String> headers = CollectionUtil.getExtraMap(
				OkConfig.HEADER_ACCESS_KEY, OkConfig.API_KEY,
				OkConfig.HEADER_ACCESS_SIGN, sign,
				OkConfig.HEADER_ACCESS_PASSPHRASE, OkConfig.V3_PASSPHRASE,
				OkConfig.HEADER_ACCESS_TIMESTAMP, timestamp
		);
		return HttpManager.get().doJsonPost(requestUrl, jsonData, headers);
	}

	public String getInstruments() {
		return doGet(OkConfig.FutureV3.INSTRUMENTS_URL, null);
	}

	public String getLeverRate(String coin) {
		String realUrl = String.format(OkConfig.FutureV3.LEVER_RATE_URL, coin);
		return doGet(realUrl, null);
	}
}
