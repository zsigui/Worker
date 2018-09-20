package sg.jackiez.worker.module.ok.network.future;

import sg.jackiez.worker.utils.annotations.NonNull;

public interface IFutureRestApi {


    /**
     * 期货行情
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     */
    String futureTicker(String symbol, String contractType);

    /**
     * 期货指数
     *
     * @param symbol btc_usd:比特币    ltc_usd :莱特币
     */
    String futureIndex(String symbol);

    /**
     * 期货交易记录
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     */
    String futureTradeHistory(String symbol, String contractType);

    /**
     * 期货交易记录(个人)
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param date         合约交割时间，格式yyyy-MM-dd
     * @param since        交易Id起始位置
     */
    String futureMyTradeHistory(String symbol, String date, String since);

    /**
     * 期货深度
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     */
    String futureDepth(String symbol, String contractType);

    /**
     * 汇率查询
     */
    String exchangeRate();

    /**
     * 取消订单
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     * @param orderId      订单ID
     */
    String futureCancelOrder(String symbol, String contractType, String orderId);

    /**
     * 期货下单
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     * @param price        价格
     * @param amount       委托数量
     * @param type         1:开多   2:开空   3:平多   4:平空
     * @param matchPrice   是否为对手价 0:不是    1:是   ,当取值为1时,price无效
     */
    String futureTrade(String symbol, String contractType, String price, String amount, String type, String matchPrice);

    /**
     * 期货批量下单
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     * @param ordersData 	JSON类型的字符串 例：[{price:5,amount:2,type:1,match_price:1},{price:2,amount:3,type:1,match_price:1}]
     *                      最大下单量为5，price,amount,type,match_price参数参考future_trade接口中的说明
     * @param leverRate 杠杆倍数，下单时无需传送，系统取用户在页面上设置的杠杆倍数。且“开仓”若有10倍多单，就不能再下20倍多单
     */
    String futureBatchTrade(String symbol, String contractType, String ordersData,
                            String leverRate);

    /**
     * 期货账户信息
     */
    String futureUserInfo();

    /**
     * 期货逐仓账户信息
     */
    String futureUserInfoForFix();

    /**
     * 用户持仓查询
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     */
    String futurePosition(String symbol, String contractType);

    /**
     * 用户逐仓持仓查询
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     */
    String futurePositionForFix(String symbol, String contractType);

    /**
     * 获取用户订单信息
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     * @param orderId      订单ID(-1查询指定状态的订单，否则查询相应单号的订单)
     * @param status       查询状态：1:未完成  2:已完成
     * @param currentPage  当前页数
     * @param pageLength   每页获取条数，最多不超过50
     */
    String futureOrderInfo(String symbol, String contractType, String orderId, String status, String currentPage, String pageLength);

    /**
     * 批量获取用户订单信息
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     * @param orderId      订单ID(多个订单ID中间以","分隔,一次最多允许查询50个订单)
     */
    String futureOrdersInfo(String symbol, String contractType, String orderId);

    /**
     * 获取合约K线数据(每个周期数据条数2000左右)
     * @param symbol 币对如btc_usdt ltc_usdt eth_usdt etc_usdt bch_usdt
     * @param contractType 合约类型: this_week:当周 next_week:下周 quarter:季度
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
     * 	            17259.83,	交易量
     * 	            79.70234956  交易量转换为指定币如 btc / ltc等
     *            ]
     */
    String futureKLine(@NonNull String symbol, @NonNull String contractType, @NonNull String type,
                       String size, String since);

    /**
     * 获取合约爆仓单
     *
     * @param symbol       btc_usd:比特币    ltc_usd :莱特币
     * @param contractType 合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
     * @param status       状态 0：最近7天未成交 1:最近7天已成交
     * @param currentPage  	当前页数索引值
     * @param pageNumber   当前页数(使用pageNumber时currentPage失效，currentPage无需传)
     * @param pageLength   每页获取条数，最多不超过50
     */
    String futureExplosiveInfo(String symbol, String contractType, String status, String currentPage,
                               String pageNumber, String pageLength);
}
