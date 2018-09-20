package sg.jackiez.worker.module.ok.network.stock;

import java.util.Map;

import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.utils.common.CollectionUtil;
import sg.jackiez.worker.utils.common.CommonUtil;
import sg.jackiez.worker.utils.http.HttpManager;
import sg.jackiez.worker.utils.http.HttpUtil;

import static sg.jackiez.worker.module.ok.OkConfig.KEY_AMOUNT;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_API_KEY;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_CURRENT_PAGE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_ORDERS_DATA;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_ORDER_ID;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_PAGE_LENGTH;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_PRICE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SIGN;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SINCE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SIZE;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_STATUS;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SYMBOL;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_TYPE;

public class StockRestApiV1 implements IStockRestApi {

    private HttpManager mHttpManager = HttpManager.get();

    @Override
    public String ticker(String symbol) {
        return mHttpManager.doGet(OkConfig.Spot.TICKER_URL, CollectionUtil.singletonMap(KEY_SYMBOL, symbol));
    }

    @Override
    public String depth(String symbol) {
        return mHttpManager.doGet(OkConfig.Spot.DEPTH_URL, CollectionUtil.singletonMap(KEY_SYMBOL, symbol));
    }

    @Override
    public String tradeHistory(String symbol, String since) {
        Map<String, String> param;
        if (!CommonUtil.isEmpty(since)) {
            param = CollectionUtil.getExtraMap(KEY_SYMBOL, symbol,
                    KEY_SINCE, since);
        } else {
            param = CollectionUtil.singletonMap(KEY_SYMBOL, symbol);
        }
        return mHttpManager.doGet(OkConfig.Spot.TRADES_URL, param);
    }

    @Override
    public String userInfo() {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Spot.USERINFO_URL, params);
    }

    @Override
    public String trade(String symbol, String type, String price, String amount) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_TYPE, type
        );
        if (!CommonUtil.isEmpty(price)) {
            params.put(KEY_PRICE, price);
        }
        if (!CommonUtil.isEmpty(amount)) {
            params.put(KEY_AMOUNT, amount);
        }
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Spot.TRADE_URL, params);
    }

    @Override
    public String batchTrade(String symbol, String type, String ordersData) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol
        );
        if (!CommonUtil.isEmpty(type)) {
            params.put(KEY_TYPE, type);
        }
        if (!CommonUtil.isEmpty(ordersData)) {
            params.put(KEY_ORDERS_DATA, ordersData);
        }
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Spot.BATCH_TRADE_URL, params);
    }

    @Override
    public String cancelOrder(String symbol, String orderId) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_ORDER_ID, orderId
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Spot.CANCEL_ORDER_URL, params);
    }

    @Override
    public String orderInfo(String symbol, String orderId) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_ORDER_ID, orderId
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Spot.ORDER_INFO_URL, params);
    }

    @Override
    public String ordersInfo(String type, String symbol, String orderId) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_TYPE, type,
                KEY_ORDER_ID, orderId
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Spot.ORDERS_INFO_URL, params);
    }

    @Override
    public String orderHistory(String symbol, String status, String currentPage, String pageLength) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_STATUS, status,
                KEY_CURRENT_PAGE, currentPage,
                KEY_PAGE_LENGTH, pageLength
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Spot.ORDER_HISTORY_URL, params);
    }

    @Override
    public String kLine(String symbol, String type, String size, String since) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_SYMBOL, symbol,
                KEY_TYPE, type
        );
        if (!CommonUtil.isEmpty(size)) {
            params.put(KEY_SIZE, size);
        }
        if (!CommonUtil.isEmpty(since)) {
            params.put(KEY_SINCE, since);
        }
        return mHttpManager.doGet(OkConfig.Spot.KLINE_URL, params);
    }

}
