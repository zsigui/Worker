package sg.jackiez.worker.module.ok.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import java.io.IOException;

import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.common.CommonUtil;

/**
 * Json处理工具
 */
public class JsonUtil {

    private static final String TAG = "JsonUtil";

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    /*============================== 特殊处理 Start ======================================*/

    public static <T> T jsonToSuccessData(String json, Class<T> type) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            SLogUtil.d(TAG, "to parse data：" + json);
            JsonNode tree = sObjectMapper.readTree(json);
            JsonNode resultNode = tree.get("result");
            if (resultNode instanceof NullNode) {
                // 数据异常
                return null;
            }

            if (resultNode.asBoolean()) {
                return jsonToObj(json, type);
            }

            SLogUtil.d(TAG, OKHelper.get().findErrorItem(tree.get("error_code").intValue()).toString());
            return null;
        } catch (Exception e) {
            SLogUtil.v(TAG, e);
        }
        return null;
    }

    public static <S> Object jsonToSuccessData(String json, String key, Class<S> type) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            SLogUtil.d(TAG, "to parse data：" + json);
            JsonNode tree = sObjectMapper.readTree(json);
            JsonNode resultNode = tree.get("result");
            if (resultNode instanceof NullNode) {
                // 数据异常
                return null;
            }

            if (resultNode.asBoolean()) {
                return jsonToObj(tree.get(key).toString(), type);
            }

            SLogUtil.d(TAG, OKHelper.get().findErrorItem(tree.get("error_code").intValue()).toString());
            return null;
        } catch (Exception e) {
            SLogUtil.v(TAG, e);
        }
        return null;
    }

    /*============================== 特殊处理 End ======================================*/

    /**
     * 将Json字符串优先转为Success实例,如果转换失败，则转为Fail实例
     */
    public static <S, F> Object jsonToBackObj(String json,
                                       Class<S> successType, Class<F> failType) {
        if (CommonUtil.isEmpty(json)) {
            return null;
        }

        try {
            return sObjectMapper.readValue(json, successType);
        } catch (IOException e) {
            return jsonToObj(json, failType);
        }
    }

    public static  <T> T jsonToObj(String json, Class<T> type) {
        try {
            return sObjectMapper.readValue(json, type);
        } catch (IOException e) {
            return null;
        }
    }
}
