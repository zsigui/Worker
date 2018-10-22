package sg.jackiez.worker.module.ok.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

    public static <T> T jsonToSuccessDataForSpot(String json, TypeReference<T> type) {
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

            ErrorItem item = OKHelper.get().findErrorItemForSpot(errorCode.intValue());
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

    public static <T> T jsonToSuccessDataForSpot(String json, String key, TypeReference<T> type) {
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

            ErrorItem item = OKHelper.get().findErrorItemForSpot(errorCode.intValue());
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

    public static <T> T jsonToSuccessDataForFuture(String json, TypeReference<T> type) {
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

            ErrorItem item = OKHelper.get().findErrorItemForFuture(errorCode.intValue());
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

    public static <T> T jsonToSuccessDataForFuture(String json, String key, TypeReference<T> type) {
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

            ErrorItem item = OKHelper.get().findErrorItemForFuture(errorCode.intValue());
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

    public static List<KlineInfo> jsonToKlineList(String json) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            JsonNode tree = sObjectMapper.readTree(json);
            if (tree.isArray()) {
                ArrayList<KlineInfo> klineInfos = new ArrayList<>();
                KlineInfo item;
                for (JsonNode node : tree) {
                    if (node == null || node.size() < 6) {
                        continue;
                    }
                    item = new KlineInfo();
                    item.time = node.get(0).asLong();
                    item.open = node.get(1).asDouble();
                    item.highest = node.get(2).asDouble();
                    item.lowest = node.get(3).asDouble();
                    item.close = node.get(4).asDouble();
                    item.volume = node.get(5).asLong();
                    if (node.size() > 6) {
                        item.currency_volume = node.get(6).asDouble();
                    }
                    klineInfos.add(item);
                }
                return klineInfos;
            }
        } catch (IOException e) {
            SLogUtil.v(TAG, e);
        }
        return null;
    }

    /*============================== 特殊处理 End ======================================*/

    /**
     * 将Json字符串优先转为Success实例,如果转换失败，则转为Fail实例
     */
    public static <S, F> Object jsonToBackObj(String json,
                                              TypeReference<S> successType, TypeReference<F> failType) {
        if (CommonUtil.isEmpty(json)) {
            return null;
        }

        try {
            return sObjectMapper.readValue(json, successType);
        } catch (IOException e) {
            return jsonToObj(json, failType);
        }
    }

    public static  <T> T jsonToObj(String json, TypeReference<T> type) {
        try {
            return sObjectMapper.readValue(json, type);
        } catch (IOException e) {
            SLogUtil.v(TAG, e);
            return null;
        }
    }

    public static String objToJson(Object obj) {
        try {
            return sObjectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            SLogUtil.v(TAG, e);
            return null;
        }
    }
}
