package sg.jackiez.worker.module.ok.network.future;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.module.ok.model.TradeInfoV3;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.SLogUtil;
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

    private FutureRestApiV3() {
    }

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

    private static String doGet(String requestUrl, Map<String, String> params) {
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
        SLogUtil.d(TAG, "doGet: data = " + data + "\nsign = " + sign + "\nheader = " + headers);
        return HttpManager.get().doGet(requestUrl, null, headers);
    }

    private static String doJsonPost(String requestUrl, Map<String, String> params,
                                     Map<String, String> contentParams) {
        String jsonData = CommonUtil.isEmpty(contentParams) ? "" : JsonUtil.objToJson(contentParams);
        String paramStr = HttpUtil.convertMapToEncodedUrlParam(params);
        if (!CommonUtil.isEmpty(paramStr)) {
            requestUrl = HttpUtil.spliceUrlAndParam(requestUrl, paramStr);
        }

        String timestamp = DateUtil.formatISOTime(System.currentTimeMillis());
        String data = preSignData(timestamp, "POST",
                requestUrl.replace(OkConfig.REST_HOST, ""), jsonData);
        String sign = Base64.getEncoder().encodeToString(
                HmacSHA256.genDigest(data, OkConfig.V3_SECRET_KEY));
        HashMap<String, String> headers = CollectionUtil.getExtraMap(
                OkConfig.HEADER_ACCESS_KEY, OkConfig.V3_API_KEY,
                OkConfig.HEADER_ACCESS_SIGN, sign,
                OkConfig.HEADER_ACCESS_PASSPHRASE, OkConfig.V3_PASSPHRASE,
                OkConfig.HEADER_ACCESS_TIMESTAMP, timestamp
        );
        SLogUtil.d(TAG, "doGet: jsonData = " + jsonData + "\ndata = " + data + "\nsign = " + sign + "\nheader = " + headers);
        return HttpManager.get().doJsonPost(requestUrl, jsonData, headers);
    }

    public static String getInstruments() {
        return doGet(OkConfig.FutureV3.INSTRUMENTS_URL, null);
    }

    public static String getKlineInfo(String instrumentId, String start, String end, String granularity) {
        String realUrl = String.format(OkConfig.FutureV3.KLINE_URL, instrumentId);
        HashMap<String, String> params = new HashMap<>();
        if (!CommonUtil.isEmpty(start)) {
            params.put(OkConfig.KEY_START, start);
        }
        if (!CommonUtil.isEmpty(end)) {
            params.put(OkConfig.KEY_END, end);
        }
        if (!CommonUtil.isEmpty(granularity)) {
            params.put(OkConfig.KEY_GRANULARITY, granularity);
        }
        return HttpManager.get().doGet(realUrl, params);
    }

    public static String getTimeStamp() {
        return HttpManager.get().doGet(OkConfig.FutureV3.TIMESTAMP_URL);
    }

    public static String getTickerInfo(String instrumentId) {
        String realUrl = String.format(OkConfig.FutureV3.TICKER_URL, instrumentId);
        return HttpManager.get().doGet(realUrl, null);
    }

    public static String getTotalTickerInfos() {
        return HttpManager.get().doGet(OkConfig.FutureV3.TOTAL_TICKERS_URL, null);
    }

    public static String getDepthInfo(String instrumentId, String size) {
        String realUrl = String.format(OkConfig.FutureV3.DEPTH_INFO_URL, instrumentId);
        Map<String, String> params = CollectionUtil.singletonMap(OkConfig.KEY_SIZE, String.valueOf(size));
        return HttpManager.get().doGet(realUrl, params);
    }

    public static String getTradeDetail(String orderId, String instrumentId, String from, String to, String limit) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                OkConfig.KEY_ORDER_ID, orderId,
                OkConfig.KEY_INSTRUMENT_ID, instrumentId
        );
        if (!CommonUtil.isEmpty(from)) {
            params.put(OkConfig.KEY_FROM, from);
        }
        if (!CommonUtil.isEmpty(to)) {
            params.put(OkConfig.KEY_TO, to);
        }
        if (!CommonUtil.isEmpty(limit)) {
            params.put(OkConfig.KEY_LIMIT, limit);
        }
        return doGet(OkConfig.FutureV3.TRADE_DETAIL_URL, params);
    }

    public static String getTradeHistory(String instrumentId, String from, String to, String limit) {
        String realUrl = String.format(OkConfig.FutureV3.TRADE_HISTORY_URL, instrumentId);
        Map<String, String> params = CollectionUtil.getExtraMap(
                OkConfig.KEY_INSTRUMENT_ID, instrumentId
        );
        if (!CommonUtil.isEmpty(from)) {
            params.put(OkConfig.KEY_FROM, from);
        }
        if (!CommonUtil.isEmpty(to)) {
            params.put(OkConfig.KEY_TO, to);
        }
        if (!CommonUtil.isEmpty(limit)) {
            params.put(OkConfig.KEY_LIMIT, limit);
        }
        return doGet(realUrl, params);
    }

    public static String getOrderList(String instrumentId, String status, String from, String to, String limit) {
        String realUrl = String.format(OkConfig.FutureV3.ORDER_LIST_URL, instrumentId);
        Map<String, String> params = CollectionUtil.getExtraMap(
                OkConfig.KEY_STATUS, status,
                OkConfig.KEY_INSTRUMENT_ID, instrumentId
        );
        if (!CommonUtil.isEmpty(to)) {
            params.put(OkConfig.KEY_FROM, from);
        }
        if (!CommonUtil.isEmpty(to)) {
            params.put(OkConfig.KEY_TO, to);
        }
        if (!CommonUtil.isEmpty(to)) {
            params.put(OkConfig.KEY_LIMIT, limit);
        }
        return doGet(realUrl, params);
    }

    public static String getOrderDetail(String orderId, String instrumentId) {
        String realUrl = String.format(OkConfig.FutureV3.ORDER_DETAIL_URL, instrumentId);
        Map<String, String> params = CollectionUtil.getExtraMap(
                OkConfig.KEY_ORDER_ID, orderId,
                OkConfig.KEY_INSTRUMENT_ID, instrumentId
        );
        return doGet(realUrl, params);
    }

    public static String doTrade(String instrumentId, String type, String price, String size,
                          String matchPrice, String leverage, String clientOID) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                OkConfig.KEY_INSTRUMENT_ID, instrumentId,
                OkConfig.KEY_TYPE, type,
                OkConfig.KEY_PRICE, price,
                OkConfig.KEY_SIZE, size,
                OkConfig.KEY_LEVERAGE, leverage
        );
        if (!CommonUtil.isEmpty(matchPrice)) {
            params.put(OkConfig.KEY_MATCH_PRICE, matchPrice);
        }
        if (!CommonUtil.isEmpty(clientOID)) {
            params.put(OkConfig.KEY_CLIENT_OID, clientOID);
        }
        return doJsonPost(OkConfig.FutureV3.TRADE_URL, null, params);
    }

    public static String doBatchTrade(String instrumentId, List<TradeInfoV3> tradeInfoList, String leverage) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                OkConfig.KEY_INSTRUMENT_ID, instrumentId,
                OkConfig.KEY_ORDERS_DATA, JsonUtil.objToJson(tradeInfoList),
                OkConfig.KEY_LEVERAGE, leverage
        );
        return doJsonPost(OkConfig.FutureV3.BATCH_TRADE_URL, null, params);
    }

    public static String doCancelTrade(String orderId, String instrumentId) {
        String realUrl = String.format(OkConfig.FutureV3.CANCEL_TRADE_URL, instrumentId, orderId);
        return doJsonPost(realUrl, null, null);
    }

    public static String doBatchCancelTrade(String instrumentId, String orderIds) {
        String realUrl = String.format(OkConfig.FutureV3.BATCH_CANCEL_TRADE_URL, instrumentId);
        Map<String, String> params = CollectionUtil.singletonMap(OkConfig.KEY_ORDER_IDS, orderIds);
        return doJsonPost(realUrl, null, params);
    }

    public static String getLeverage(String currency) {
        String realUrl = String.format(OkConfig.FutureV3.LEVERAGE_URL, currency);
        return doGet(realUrl, null);
    }

    public static String setLeverage(String currency, String leverage,
                              String instrumentId, String direction) {
        String realUrl = String.format(OkConfig.FutureV3.LEVERAGE_URL, currency);
        Map<String, String> params = CollectionUtil.getExtraMap(
                OkConfig.KEY_LEVERAGE, leverage
        );
        // 后面两个逐仓也得填
        if (!CommonUtil.isEmpty(instrumentId)) {
            params.put(OkConfig.KEY_INSTRUMENT_ID, instrumentId);
        }
        if (!CommonUtil.isEmpty(direction)) {
            params.put(OkConfig.KEY_DIRECTION, direction);
        }
        return doJsonPost(realUrl, null, params);
    }

    public static String getUserInfo() {
        return doGet(OkConfig.FutureV3.USER_INFO_URL, null);
    }

    public static String getAllPositionInfo() {
        return doGet(OkConfig.FutureV3.ALL_POSITION_URL, null);
    }

    public static String getPositionInfo(String instrumentId) {
        String realUrl = String.format(OkConfig.FutureV3.POSITION_URL, instrumentId);
        return doGet(realUrl, null);
    }
}
