package sg.jackiez.worker.module.ok;

import java.net.Proxy;

import sg.jackiez.worker.utils.http.ProxyInfo;

public final class OkConfig {

    // 请求的Key
    public static final String KEY_SYMBOL = "symbol";
    public static final String KEY_SINCE = "since";
    public static final String KEY_API_KEY = "api_key";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PRICE = "price";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_SIGN = "sign";
    public static final String KEY_ORDERS_DATA = "orders_data";
    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CURRENT_PAGE = "current_page";
    public static final String KEY_PAGE_LENGTH = "page_length";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_SIZE = "size";
    public static final String KEY_CONTRACT_TYPE = "contract_type";
    public static final String KEY_DATE = "date";
    public static final String KEY_LEVER_RATE = "lever_rate";
    public static final String KEY_PAGE_NUMBER = "page_number";
    public static final String KEY_MATCH_PRICE = "match_price";

    // 返回的部分Key
    /**
     * 美元-人民币汇率
     */
    public static final String RESP_KEY_RATE = "rate";
    /**
     * 合约指数
     */
    public static final String RESP_KEY_FUTURE_INDEX = "future_index";
    /**
     * 交割预估期（注意：交割预估价只有交割前三小时返回）
     */
    public static final String RESP_KEY_FORECAST_PRICE = "forecast_price";

    // 相关文件位置
    /**
     * 现货交易接口错误码文件
     */
    public static final String FILE_SPOT_ERROR = "data/ok/spot_cn_rest_error";
    /**
     * 期货交易接口错误码文件
     */
    public static final String FILE_FUTURE_ERROR = "data/ok/future_cn_rest_error";
    /**
     * 交易对信息
     */
    public static final String FILE_PAIRS_INCREMENT = "data/ok/pairs_increment";


    // 相关Wiki地址：https://github.com/okcoin-okex/API-docs-OKEx.com

//    public static String API_KEY = "8a4d0dbb-8f1d-4c25-a238-ef41d6e75ba8";
//    public static String SECRET_KEY = "074AD4B323E4ECB5190C63648643FA0D";
    public static String API_KEY = "7a082e8e-cf90-4450-82fd-3063009ae88b";
    public static String SECRET_KEY = "FD63F9C95FAA082F761797C7B25890E7";
    /**
     * 请求的REST地址
     */
    private static String REST_HOST = "https://www.okex.com";
//    private static String REST_HOST = "https://www.okb.com";
    public static String WSS_URL = "wss://real.okex.com:10440/websocket/okexapi";

    // 配置是否适用代理
    public static final boolean IS_USE_PROXY = false;
    public static final ProxyInfo PROXY_INFO = new ProxyInfo(Proxy.Type.HTTP, "127.0.0.1", 8080);


    private static String totalUrl(String path) {
        return REST_HOST + path;
    }

    /**
     * 用户账户的URL
     */
    public interface Account {

        // 相关wiki：https://github.com/okcoin-okex/API-docs-OKEx.com/blob/master/API-For-Spot-CN/%E5%B8%81%E5%B8%81%E4%BA%A4%E6%98%93REST%20API.md

        /**
         * 资金划转
         */
        String FUNDS_TRANSFER_URL = totalUrl("/api/v1/funds_transfer.do");

        /**
         * 获取用户钱包账户信息，频率 6次/2秒
         */
        String WALLET_INFO = totalUrl("/api/v1/wallet_info.do");

        /**
         * 个人账户资金划转，币币和合约互转
         */
        String DEVOLVE_URL = totalUrl("/api/v1/future_devolve.do");

    }

    /**
     * 现货的URL
     */
    public interface Spot {

        // 相关wiki：https://github.com/okcoin-okex/API-docs-OKEx.com/blob/master/API-For-Spot-CN/%E5%B8%81%E5%B8%81%E4%BA%A4%E6%98%93REST%20API.md

        /**
         * 现货行情URL
         */
        String TICKER_URL = totalUrl("/api/v1/ticker.do");

        /**
         * 现货市场深度URL
         */
        String DEPTH_URL = totalUrl("/api/v1/depth.do");

        /**
         * 现货历史交易信息URL
         */
        String TRADES_URL = totalUrl("/api/v1/trades.do");

        /**
         * 现货获取用户信息URL
         */
        String USERINFO_URL = totalUrl("/api/v1/userinfo.do");

        /**
         * 现货下单交易URL
         */
        String TRADE_URL = totalUrl("/api/v1/trade.do");

        /**
         * 现货批量下单URL
         */
        String BATCH_TRADE_URL = totalUrl("/api/v1/batch_trade.do");

        /**
         * 现货撤销订单URL
         */
        String CANCEL_ORDER_URL = totalUrl("/api/v1/cancel_order.do");

        /**
         * 现货获取用户订单URL
         */
        String ORDER_INFO_URL = totalUrl("/api/v1/order_info.do");

        /**
         * 现货批量获取用户订单URL
         */
        String ORDERS_INFO_URL = totalUrl("/api/v1/orders_info.do");

        /**
         * 现货获取历史订单信息，只返回最近七天的信息URL
         */
        String ORDER_HISTORY_URL = totalUrl("/api/v1/order_history.do");

        /**
         * 获取OKEx币币K线数据(每个周期数据条数2000左右)
         */
        String KLINE_URL = totalUrl("/api/v1/kline.do");
    }

    /**
     * 期货的URL
     */
    public interface Future {
        /**
         * 期货行情URL
         */
        String TICKER_URL = totalUrl("/api/v1/future_ticker.do");
        /**
         * 期货指数查询URL
         */
        String INDEX_URL = totalUrl("/api/v1/future_index.do");

        /**
         * 期货交易记录查询URL
         */
        String TRADES_URL = totalUrl("/api/v1/future_trades.do");

        /**
         * 期货市场深度查询URL
         */
        String DEPTH_URL = totalUrl("/api/v1/future_depth.do");
        /**
         * 美元-人民币汇率查询URL
         */
        String EXCHANGE_RATE_URL = totalUrl("/api/v1/exchange_rate.do");

        /**
         * 期货取消订单URL
         */
        String CANCEL_ORDER_URL = totalUrl("/api/v1/future_cancel.do");

        /**
         * 期货下单URL(访问频率 20次/2秒(按币种单独计算)
         */
        String TRADE_URL = totalUrl("/api/v1/future_trade.do");

        /**
         * 期货批量下单URL(访问频率 10次/2秒 最多一次下1-5个订单（按币种单独计算）)
         */
        String BATCH_TRADE_URL = totalUrl("/api/v1/future_batch_trade.do");

        /**
         * 获取合约交易历史（访问频率 1次/120秒）
         */
        String PUBLIC_TRADES_URL = totalUrl("/api/v1/future_trades_history.do");

        /**
         * 期货账户信息URL
         */
        String USERINFO_URL = totalUrl("/api/v1/future_userinfo.do");

        /**
         * 逐仓期货账户信息URL
         */
        String USERINFO_4FIX_URL = totalUrl("/api/v1/future_userinfo_4fix.do");

        /**
         * 期货持仓查询URL(访问频率 10次/2秒)
         */
        String POSITION_URL = totalUrl("/api/v1/future_position.do");

        /**
         * 期货逐仓持仓查询URL
         */
        String POSITION_4FIX_URL = totalUrl("/api/v1/future_position_4fix.do");

        /**
         * 用户期货订单信息查询URL
         */
        String ORDER_INFO_URL = totalUrl("/api/v1/future_order_info.do");

        /**
         * 期货批量获取用户订单URL
         */
        String ORDERS_INFO_URL = totalUrl("/api/v1/future_orders_info.do");

        /**
         * 获取OKEx合约K线数据
         */
        String KLINE_URL = totalUrl("/api/v1/future_kline.do");

        /**
         * 获取合约爆仓单
         */
        String EXPLOSIVE_URL = totalUrl("/api/v1/future_explosive.do");

        /**
         * 获取当前可用合约总持仓量
         */
        String FUTURE_HOLD_AMOUNT_URL = totalUrl("/api/v1/future_hold_amount.do");
    }
}
