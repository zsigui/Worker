package sg.jackiez.worker.module.ok;

public class OKTypeConfig {

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
	 * 撤单处理中
	 */
	public static final int STATUS_CANCELING = 3;

	/**
	 * 未完成订单类型
	 */
	public static final int ORDER_TYPE_UNFINISH = 0;
	/**
	 * 已完成订单类型
	 */
	public static final int ORDER_TYPE_FINISHED = 1;

	public static final String KLINE_TYPE_1_MIN = "1min";
	public static final String KLINE_TYPE_3_MIN = "3min";
	public static final String KLINE_TYPE_5_MIN = "5min";
	public static final String KLINE_TYPE_15_MIN = "15min";
	public static final String KLINE_TYPE_30_MIN = "30min";
	public static final String KLINE_TYPE_1_DAY = "1day";
	public static final String KLINE_TYPE_1_WEEK = "1week";
	public static final String KLINE_TYPE_1_HOUR = "1hour";
	public static final String KLINE_TYPE_2_HOUR = "2hour";
	public static final String KLINE_TYPE_4_HOUR = "4hour";
	public static final String KLINE_TYPE_6_HOUR = "6hour";
	public static final String KLINE_TYPE_12_HOUR = "12hour";


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
	public static final byte ACCOUNT_TYPE_4_FIX = 0;
	/**
	 * 全仓账户
	 */
	public static final byte ACCOUNT_TYPE_All = 1;

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
}
