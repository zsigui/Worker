package sg.jackiez.worker.module.ok;

public class OKTypeConfig {

	// 自定义数据库订单处理状态
	/**
	 * 初始化状态
	 */
	public static final int DB_STATE_INIT = 0;
	/**
	 * 表示下单成功，交易中，需要后续查询更新状态
	 */
	public static final int DB_STATE_TRADING = 1;
	/**
	 * 部分交易完成状态，未完成部分，可撤单
	 */
	public static final int DB_STATE_PART_TRADE = 2;
	/**
	 * 完全交易完成状态，不可撤单
	 */
	public static final int DB_STATE_FULL_TRADE = 3;
	/**
	 * 取消订单处理中
	 */
	public static final int DB_STATE_CANCELLING = 4;
	/**
	 * 取消订单完成
	 */
	public static final int DB_STATE_CANCELLED = 5;


	// 期货币对
	public static final String SYMBOL_EOS = "eos_usd";
	public static final String SYMBOL_BTC = "btc_usd";

	/**
	 * 买入
	 */
	public static final String TYPE_BUY = "buy";
	/**
	 * 卖出
	 */
	public static final String TYPE_SELL = "sell";
	/**
	 * 市价买入
	 */
	public static final String TYPE_BUY_MARKET = "buy_market";
	/**
	 * 市价卖出
	 */
	public static final String TYPE_SELL_MARKET = "sell_market";

	/**
	 * 已撤销
	 */
	public static final int STATUS_CANCELED = -1;
	/**
	 * 未成交
	 */
	public static final int STATUS_NOT_TRANSACT = 0;
	/**
	 * 部分成交
	 */
	public static final int STATUS_PART_TRANSACT = 1;
	/**
	 * 完全成交
	 */
	public static final int STATUS_FULL_TRANSACT = 2;

	/**
	 * 未完成订单类型
	 */
	public static final int ORDER_TYPE_UNFINISH = 0;
	/**
	 * 已完成订单类型
	 */
	public static final int ORDER_TYPE_FINISHED = 1;

	public static final String KLINE_TYPE_1_MIN = "60";
	public static final String KLINE_TYPE_3_MIN = "180";
	public static final String KLINE_TYPE_5_MIN = "300";
	public static final String KLINE_TYPE_15_MIN = "900";
	public static final String KLINE_TYPE_30_MIN = "1800";
	public static final String KLINE_TYPE_1_HOUR = "3600";
	public static final String KLINE_TYPE_2_HOUR = "7200";
	public static final String KLINE_TYPE_4_HOUR = "14400";
	public static final String KLINE_TYPE_6_HOUR = "21600";
	public static final String KLINE_TYPE_12_HOUR = "43200";
	public static final String KLINE_TYPE_1_DAY = "86400";
	public static final String KLINE_TYPE_1_WEEK = "604800";


	public static final byte SITE_FLAG_CNY = 0;
	public static final byte SITE_FLAG_USDT = 1;

	// 订阅的事件类型
	// 现货
	/**
	 * 用于订阅行情数据
	 */
	public static final String SUB_EVENT_TYPE_TICKER = "ok_sub_spot_%s_ticker";
	/**
	 * 用于订阅市场深度数据（200增量数据返回）<br />
	 * 第一次返回全量数据，后续在该基础上执行如下操作：删除（量为0时）；修改（价格相同量不同）；增加（价格不存在）。
	 */
	public static final String SUB_EVENT_TYPE_DEPTH = "ok_sub_spot_%s_depth";
	/**
	 * 用于订阅市场深度数据，指定获取深度条数，如5/10/20
	 */
	public static final String SUB_EVENT_TYPE_DEPTH_WITH_SIZE = "ok_sub_spot_%s_depth_%d";
	/**
	 * 用于订阅成交记录数据
	 */
	public static final String SUB_EVENT_TYPE_TRADE_HISTORY = "ok_sub_spot_%s_deals";
	/**
	 * 用于订阅K线数据，前为币对，后为K线周期，如1min/1hour
	 */
	public static final String SUB_EVENT_TYPE_KLINE = "ok_sub_spot_%s_kline_%s";
	// 期货
	/**
	 * 订阅合约行情
	 */
	public static final String SUB_EVENT_TYPE_FUTURE_TICKER = "ok_sub_futureusdt_%s_ticker_%s";
	/**
	 *  订阅合约K线数据
	 */
	public static final String SUB_EVENT_TYPE_FUTURE_KLINE = "ok_sub_futureusdt_%s_kline_%s_%s";
	/**
	 * 订阅合约市场深度(200增量数据返回)
	 */
	public static final String SUB_EVENT_TYPE_FUTURE_DEPTH = "ok_sub_futureusdt_%s_depth_%s";
	/**
	 * 订阅合约市场深度数据，指定获取深度条数，如5/10/20（全量返回）
	 */
	public static final String SUB_EVENT_TYPE_FUTURE_DEPTH_WITH_SIZE = "ok_sub_futureusdt_%s_depth_%s_%s";
	/**
	 * 订阅合约交易信息
	 */
	public static final String SUB_EVENT_TYPE_FUTURE_TRADE = "ok_sub_futureusdt_%s_trade_%s";
	/**
	 * 订阅合约指数
	 */
	public static final String SUB_EVENT_TYPE_FUTURE_INDEX = "ok_sub_futureusdt_%s_index";
	/**
	 * 订阅合约预估交割价格
	 */
	public static final String SUB_EVENT_TYPE_FUTURE_FORCAST_PRICE = "%s_forecast_price";

	// 合约类型
	/**
	 * 当周合约
	 */
	public static final String CONTRACT_TYPE_THIS_WEEK = "this_week";
	/**
	 * 下周合约
	 */
	public static final String CONTRACT_TYPE_NEXT_WEEK = "next_week";
	/**
	 * 季度合约
	 */
	public static final String CONTRACT_TYPE_QUARTER = "quarter";

	// 趋势类型
	/**
	 * 开多
	 */
	public static final byte TREND_TYPE_BUY_LONG = 1;
	/**
	 * 开空
	 */
	public static final byte TREND_TYPE_BUY_SHORT = 2;
	/**
	 * 平多
	 */
	public static final byte TREND_TYPE_SELL_LONG = 3;
	/**
	 * 平空
	 */
	public static final byte TREND_TYPE_SELL_SHORT = 4;

	// 市价及非市价类型
	/**
	 * 指定价格买入
	 */
	public static final String PRICE_TYPE_PARTILY_PRICE = "0";
	/**
	 * 市价买入
	 */
	public static final String PRICE_TYPE_MARKET_PRICE = "1";

	// 杠杆倍数
	/**
	 * 10倍
	 */
	public static final String LEVER_RATE_10 = "10";
	/**
	 * 20倍
	 */
	public static final String LEVER_RATE_20 = "20";

	// 账户类型
	/**
	 * 逐仓账户
	 */
	public static final String ACCOUNT_TYPE_4_FIXED = "fixed";
	/**
	 * 全仓账户
	 */
	public static final String ACCOUNT_TYPE_CROSSED = "crossed";

	// 钱包类型
	/**
	 * 钱包类型：币币账户
	 */
	public static final int FUNC_TYPE_SPOT = 1;
	/**
	 * 钱包类型：合约账户
	 */
	public static final int FUNC_TYPE_FUTURE = 3;
	/**
	 * 钱包类型：我的钱包
	 */
	public static final int FUNC_TYPE_WALLET = 6;

	// 开仓方向
	/**
	 * 开仓做多
	 */
	public static final String DIRECTION_LONG = "long";
	/**
	 * 开仓做空
	 */
	public static final String DIRECTION_SHORT = "short";
}
