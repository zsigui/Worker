package sg.jackiez.worker.module.ok.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.module.ok.model.ErrorItem;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.common.CommonUtil;

/**
 * Json处理工具
 */
public class JsonUtil {

    private static final String TAG = "JsonUtil";

    private static ObjectMapper sObjectMapper = new ObjectMapper();
    static {
        sObjectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        sObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /*============================== 特殊处理 Start ======================================*/

    public static <T> T jsonToSuccessData(String json, Class<T> type) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            SLogUtil.d(TAG, "to parse data：" + json);
            JsonNode tree = sObjectMapper.readTree(json);
            JsonNode errorCode = tree.get("error_code");

            if (errorCode == null || (errorCode instanceof NullNode)) {
                return jsonToObj(json, type);
            }

            ErrorItem item = OKHelper.get().findErrorItem(errorCode.intValue());
            if (item != null) {
                SLogUtil.d(TAG, item);
            } else {
                SLogUtil.d(TAG, "no available code : " + errorCode.intValue());
            }
            return null;
        } catch (Exception e) {
            SLogUtil.v(TAG, e);
        }
        return null;
    }

    public static <T> T jsonToSuccessData(String json, String key, Class<T> type) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            SLogUtil.d(TAG, "to parse data：" + json);
            JsonNode tree = sObjectMapper.readTree(json);
            JsonNode errorCode = tree.get("error_code");

            if (errorCode == null || (errorCode instanceof NullNode)) {
                return jsonToObj(tree.get(key).toString(), type);
            }

            ErrorItem item = OKHelper.get().findErrorItem(errorCode.intValue());
            if (item != null) {
                SLogUtil.d(TAG, item);
            } else {
                SLogUtil.d(TAG, "no available code : " + errorCode.intValue());
            }
            return null;
        } catch (Exception e) {
            SLogUtil.v(TAG, e);
        }
        return null;
    }

    public static List<KlineInfo> jsonToKlinkData(String json) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            ArrayList<KlineInfo> result = new ArrayList<>();
            JsonNode node = sObjectMapper.readTree(json);
            if (node.isArray()) {
                KlineInfo tmp;
                for (JsonNode child : node) {
                    tmp = new KlineInfo();
                    tmp.time = child.get(0).asLong();
                    tmp.open = child.get(1).asDouble();
                    tmp.highest = child.get(2).asDouble();
                    tmp.lowest = child.get(3).asDouble();
                    tmp.close = child.get(4).asDouble();
                    tmp.volumn = child.get(5).asDouble();
                    result.add(tmp);
                }
                return result;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
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

    public static String objToJson(Object obj) {
        try {
            return sObjectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
