package sg.jackiez.worker.module.ok.network.stock;

import sg.jackiez.worker.utils.annotations.NonNull;
import sg.jackiez.worker.utils.annotations.Nullable;

/**
 * 现货行情，交易 REST API
 */
public interface IStockRestApi {

    /**
     * 行情
     *
     * @param symbol btc_usd:比特币    ltc_usd :莱特币
     */
    String ticker(@NonNull String symbol);

    /**
     * 市场深度
     *
     * @param symbol btc_usd:比特币    ltc_usd :莱特币
     */
    String depth(@NonNull String symbol);

    /**
     * 现货历史交易信息
     *
     * @param symbol btc_usd:比特币    ltc_usd :莱特币
     * @param since  指定的交易记录Id, 不加since参数时，返回最近的60笔交易
     */
    String tradeHistory(@NonNull String symbol, @Nullable String since);

    /**
     * 获取用户信息
     */
    String userInfo();

    /**
     * 下单交易
     *
     * @param symbol btc_usd: 比特币 ltc_usd: 莱特币
     * @param type   买卖类型： 限价单（buy/sell） 市价单（buy_market/sell_market）
     * @param price  下单价格 [限价买单(必填)： 大于等于0，小于等于1000000 |
     *               市价买单(必填)： BTC :最少买入0.01个BTC 的金额(金额>0.01*卖一价) / LTC :最少买入0.1个LTC 的金额(金额>0.1*卖一价)]
     * @param amount 交易数量 [限价卖单（必填）：BTC 数量大于等于0.01 / LTC 数量大于等于0.1 |
     *               市价卖单（必填）： BTC :最少卖出数量大于等于0.01 / LTC :最少卖出数量大于等于0.1]
     */
    String trade(@NonNull String symbol, @NonNull String type, @Nullable String price, @Nullable String amount);

    /**
     * 批量下单
     *
     * @param symbol      btc_usd: 比特币 ltc_usd: 莱特币
     * @param type        买卖类型： 限价单（buy/sell） 市价单（buy_market/sell_market）
     * @param ordersData JSON类型的字符串 例：[{price:3,amount:5},{price:3,amount:3}]
     *                    最大下单量为5，price和amount参数参考trade接口中的说明
     */
    String batchTrade(@NonNull String symbol, @Nullable String type, @NonNull String ordersData);

    /**
     * 撤销订单
     *
     * @param symbol   btc_usd: 比特币 ltc_usd: 莱特币
     * @param orderId 订单ID(多个订单ID中间以","分隔,一次最多允许撤消3个订单)
     */
    String cancelOrder(@NonNull String symbol, @NonNull String orderId);

    /**
     * 获取用户的订单信息
     *
     * @param symbol   btc_usd: 比特币 ltc_usd: 莱特币
     * @param orderId 订单ID(-1查询全部订单，否则查询相应单号的订单)
     */
    String orderInfo(@NonNull String symbol, @NonNull String orderId);

    /**
     * 批量获取用户订单
     *
     * @param type     查询类型 0:未成交，未成交 1:完全成交，已撤销
     * @param symbol   btc_usd: 比特币 ltc_usd: 莱特币
     * @param orderId 订单ID(多个订单ID中间以","分隔,一次最多允许查询50个订单)
     */
    String ordersInfo(@NonNull String type, @NonNull String symbol, @NonNull String orderId);

    /**
     * 获取历史订单信息，只返回最近七天的信息
     *
     * @param symbol       btc_usd: 比特币 ltc_usd: 莱特币
     * @param status       委托状态: 0：未成交 1：已完成(最近七天的数据)
     * @param currentPage 当前页数
     * @param pageLength  每页数据条数，最多不超过200
     */
    String orderHistory(@NonNull String symbol, @NonNull String status,
                         @NonNull String currentPage, @NonNull String pageLength);


    /**
     * 获取OKEx币币K线数据(每个周期数据条数2000左右)
     * @param symbol 币对如ltc_btc
     * @param type 1min/3min/5min/15min/30min/1day/1week/1hour/2hour/4hour/6hour/12hour
     * @param size 非必须，指定获取数据条数
     * @param since 时间戳，返回该时间戳以后数据 ms
     * @return
     *           [
     * 	            1417536000000,	时间戳
     * 	            2370.16,	开
     * 	            2380,		高
     * 	            2352,		低
     * 	            2367.37,	收
     * 	            17259.83	交易量
     *            ]
     */
    String kLine(@NonNull String symbol, @NonNull String type,
                 String size, String since);
}
