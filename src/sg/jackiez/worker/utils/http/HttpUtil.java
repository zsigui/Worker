package sg.jackiez.worker.utils.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.utils.Config;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.chiper.MD5;
import sg.jackiez.worker.utils.common.CommonUtil;

public class HttpUtil {

    private static final String TAG = "HttpUtil";

    public static String createOkSignByParam(Map<String, String> param, String secretKey) {
        String sign = "";
        try {
            String urlParam = convertMapToSortedUrlParam(param);
            urlParam = urlParam + "&secret_key=" + secretKey;
            SLogUtil.d(TAG, "sign = " + urlParam);
            sign = CommonUtil.bytesToHex(MD5.genDigest(urlParam));
            SLogUtil.d(TAG, "sign = " + sign);
        } catch (Exception e) {
            SLogUtil.e(TAG, e);
        }
        return sign;
    }

    public static String convertMapToSortedUrlParam(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            List<String> keys = new ArrayList<String>(params.keySet());
            Collections.sort(keys);
            // 拼装参数
            for (String key : keys) {
                builder.append(key).append('=').append(params.get(key)).append('&');
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static String convertMapToEncodedUrlParam(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            // 拼装参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    builder.append(URLEncoder.encode(entry.getKey(), Config.DEFAULT_SYS_CHARSET))
                            .append('=')
                            .append(URLEncoder.encode(entry.getValue(), Config.DEFAULT_SYS_CHARSET))
                            .append('&');
                } catch (UnsupportedEncodingException e) {
                    SLogUtil.e(TAG, e);
                }
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static String spliceUrlAndParam(String url, String paramStr) {
        int i = url.lastIndexOf('?');
        if (i == -1) {
            url += '?' + paramStr;
        } else if (i == url.length() - 1) {
            url += paramStr;
        } else {
            if (url.charAt(url.length() - 1) == '&') {
                url += paramStr;
            } else {
                url += '&' + paramStr;
            }
        }
        return url;
    }
}
