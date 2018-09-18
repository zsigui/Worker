package sg.jackiez.worker.utils.algorithm.bean;

import sg.jackiez.worker.utils.DateUtil;

/**
 * @Author JackieZ
 * @Date Created on 2018/9/16
 */
public class KlineInfo {

	/**
	 * 时间
	 */
	public long time;
	/**
	 * 开盘价
	 */
	public double open;
	/**
	 * 最高价
	 */
	public double highest;
	/**
	 * 最低价
	 */
	public double lowest;
	/**
	 * 收盘价
	 */
	public double close;
	/**
	 * 交易量
	 */
	public double volume;

	@Override
	public String toString() {
		return "{" +
				"time=" + DateUtil.formatUnixTime(time) +
				",open=" + open +
				",close=" + close +
				",lowest=" + lowest +
				",highest=" + highest +
				",volume=" + volume +
				'}';
	}
}
