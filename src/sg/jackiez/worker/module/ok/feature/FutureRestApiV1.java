package sg.jackiez.worker.module.ok.feature;

import java.util.Map;

import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.utils.common.CollectionUtil;
import sg.jackiez.worker.utils.common.CommonUtil;
import sg.jackiez.worker.utils.http.HttpManager;
import sg.jackiez.worker.utils.http.HttpUtil;

import static sg.jackiez.worker.module.ok.OkConfig.KEY_AMOUNT;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_API_KEY;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_CONTRACT_TYPE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_CURRENT_PAGE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_DATE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_LEVER_RATE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_ORDERS_DATA;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_ORDER_ID;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_PAGE_LENGTH;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_PAGE_NUMBER;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_PRICE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SIGN;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SINCE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SIZE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_STATUS;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SYMBOL;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_TYPE;

public class FutureRestApiV1 implements IFutureRestApi{

    private HttpManager mHttpManager = HttpManager.get();

    @Override
    public String futureTicker(String symbol, String contractType) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType);
        return mHttpManager.doGet(OkConfig.Future.TICKER_URL, params);
    }

    @Override
    public String futureIndex(String symbol) {
        return mHttpManager.doGet(OkConfig.Future.INDEX_URL, CollectionUtil.singletonMap(KEY_SYMBOL, symbol));
    }

    @Override
    public String futureTradeHistory(String symbol, String contractType) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType);
        return mHttpManager.doGet(OkConfig.Future.TRADES_URL, params);
    }

    @Override
    public String futureMyTradeHistory(String symbol, String date, String since) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_DATE, date,
                KEY_SINCE, since
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.PUBLIC_TRADES_URL, params);
    }

    @Override
    public String futureDepth(String symbol, String contractType) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType);
        return mHttpManager.doGet(OkConfig.Future.DEPTH_URL, params);
    }

    @Override
    public String exchangeRate() {
        return mHttpManager.doGet(OkConfig.Future.EXCHANGE_RATE_URL, null);
    }

    @Override
    public String futureCancelOrder(String symbol, String contractType, String orderId) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_ORDER_ID, orderId,
                KEY_CONTRACT_TYPE, contractType
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.CANCEL_ORDER_URL, params);
    }

    @Override
    public String futureTrade(String symbol, String contractType, String price, String amount, String type, String matchPrice) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_TYPE, type,
                KEY_CONTRACT_TYPE, contractType
        );
        if (!CommonUtil.isEmpty(price)) {
            params.put(KEY_PRICE, price);
        }
        if (!CommonUtil.isEmpty(amount)) {
            params.put(KEY_AMOUNT, amount);
        }
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.TRADE_URL, params);
    }

    @Override
    public String futureBatchTrade(String symbol, String contractType, String ordersData, String leverRate) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType,
                KEY_ORDERS_DATA, ordersData
        );
        if (!CommonUtil.isEmpty(leverRate)) {
            params.put(KEY_LEVER_RATE, leverRate);
        }
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.BATCH_TRADE_URL, params);
    }

    @Override
    public String futureUserInfo() {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.USERINFO_URL, params);
    }

    @Override
    public String futureUserInfoForFix() {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.USERINFO_4FIX_URL, params);
    }

    @Override
    public String futurePosition(String symbol, String contractType) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.POSITION_URL, params);
    }

    @Override
    public String futurePositionForFix(String symbol, String contractType) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.POSITION_4FIX_URL, params);
    }

    @Override
    public String futureOrderInfo(String symbol, String contractType, String orderId, String status,
                                  String currentPage, String pageLength) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType
        );
        params.put(KEY_ORDER_ID, CommonUtil.isEmpty(orderId) ? "-1" : orderId);
        if (!CommonUtil.isEmpty(status)) {
            params.put(KEY_STATUS, status);
        }
        if (!CommonUtil.isEmpty(currentPage)) {
            params.put(KEY_CURRENT_PAGE, currentPage);
        }
        if (!CommonUtil.isEmpty(pageLength)) {
            params.put(KEY_PAGE_LENGTH, pageLength);
        }
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.ORDER_INFO_URL, params);
    }

    @Override
    public String futureOrdersInfo(String symbol, String contractType, String orderId) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType,
                KEY_ORDER_ID, orderId
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.ORDERS_INFO_URL, params);
    }

    @Override
    public String futureKLine(String symbol, String contractType, String type, String size, String since) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType,
                KEY_TYPE, type
        );
        if (!CommonUtil.isEmpty(size)) {
            params.put(KEY_SIZE, size);
        }
        if (!CommonUtil.isEmpty(since)) {
            params.put(KEY_SINCE, since);
        }
        return mHttpManager.doGet(OkConfig.Future.KLINE_URL, params);
    }

    @Override
    public String futureExplosiveInfo(String symbol, String contractType, String status, String currentPage,
                                      String pageNumber, String pageLength) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_CONTRACT_TYPE, contractType
        );
        if (!CommonUtil.isEmpty(status)) {
            params.put(KEY_STATUS, status);
        }
        if (!CommonUtil.isEmpty(currentPage)) {
            params.put(KEY_CURRENT_PAGE, currentPage);
        }
        if (!CommonUtil.isEmpty(pageNumber)) {
            params.put(KEY_PAGE_NUMBER, pageNumber);
        }
        if (!CommonUtil.isEmpty(pageLength)) {
            params.put(KEY_PAGE_LENGTH, pageLength);
        }
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Future.EXPLOSIVE_URL, params);
    }
}
