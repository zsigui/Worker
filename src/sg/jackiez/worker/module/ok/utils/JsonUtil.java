package sg.jackiez.worker.module.ok.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.model.ErrorItem;
import sg.jackiez.worker.module.ok.model.Leverage;
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
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
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
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
        }
        return null;
    }

    public static <T> T jsonToSuccessDataForFutureWithErrCode(String json, TypeReference<T> type) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            SLogUtil.d(TAG, "to parse data：" + json);
            JsonNode tree = sObjectMapper.readTree(json);
            JsonNode errorCode = tree.get("error_code");

            if (errorCode == null || (errorCode instanceof NullNode)
                    || errorCode.intValue() == 0) {
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
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
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

            SLogUtil.i(TAG, "to be parsed error data : " + json);
            ErrorItem item = OKHelper.get().findErrorItemForFuture(errorCode.intValue());
            if (item != null) {
                SLogUtil.d(TAG, item);
            } else {
                SLogUtil.d(TAG, "no available code : " + errorCode.intValue());
            }
            return null;
        } catch (Exception e) {
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
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

            SLogUtil.i(TAG, "to be parsed error data : " + json);
            ErrorItem item = OKHelper.get().findErrorItemForFuture(errorCode.intValue());
            if (item != null) {
                SLogUtil.d(TAG, item);
            } else {
                SLogUtil.d(TAG, "no available code : " + errorCode.intValue());
            }
            return null;
        } catch (Exception e) {
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
        }
        return null;
    }

    public static List<KlineInfo> jsonToKlineList(String json, boolean isSortFromSmall) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            JsonNode tree = sObjectMapper.readTree(json);
            if (tree.isArray()) {
                LinkedList<KlineInfo> klineInfos = new LinkedList<>();
                KlineInfo item;
                int volumeIndex = -1;
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
                    if (item.volume == 0) {
                        // OK的机制，比如当前12:00:03，此时可以拿到12:00:00~12:00:59的数据，但实际拿到的量为0
                        if (isSortFromSmall) {
                            volumeIndex = klineInfos.size() - 1;
                        } else if (klineInfos.size() == 1) {
                            volumeIndex = 0;
                        }
                        SLogUtil.i(TAG, "Filter 0 volume K-Line data : " + json);
                    }
                    klineInfos.add(item);
                }

                if (volumeIndex != -1 && (!isSortFromSmall || volumeIndex == klineInfos.size() - 1)) {
                    // 从小到大排序的话，如果是最后一个数据，且量为0，表示还没获取到有效数据，移除掉
                    // 从大到小排序的话，如果是第一个数据，且量为0，表示还没获取到有效数据，移除掉
                    klineInfos.remove(volumeIndex);
                }
                return klineInfos;
            }
        } catch (IOException e) {
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
        }
        return null;
    }

    public static Leverage jsonToLeverage(String json, String instrumentId) {
        if (CommonUtil.isEmpty(json)) {
            SLogUtil.v(TAG, "json is null or empty.");
            return null;
        }
        try {
            JsonNode tree = sObjectMapper.readTree(json);
            Leverage leverage = new Leverage();
            leverage.margin_mode = tree.get("margin_mode").asText("");
            if (OKTypeConfig.ACCOUNT_TYPE_4_FIXED.equalsIgnoreCase(leverage.margin_mode)) {
                leverage.key = instrumentId;
                JsonNode node = tree.get(instrumentId);
                if (node != null) {
                    leverage.long_leverage = node.get("long_leverage").asInt();
                    leverage.short_leverage = node.get("short_leverage").asInt();
                }
            } else {
                leverage.key = tree.get("currency").asText("");
                leverage.long_leverage = leverage.short_leverage = tree.get("leverage").asInt();
            }
            return leverage;
        } catch (Exception e) {
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
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

    public static <T> T jsonToObj(String json, TypeReference<T> type) {
        try {
            return sObjectMapper.readValue(json, type);
        } catch (IOException e) {
            SLogUtil.i(TAG, "parse json : " + json + ", error : " + e);
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
