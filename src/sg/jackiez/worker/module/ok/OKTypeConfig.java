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
}
