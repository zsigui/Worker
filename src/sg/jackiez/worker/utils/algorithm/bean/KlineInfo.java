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
	/**
	 * 按币种折算的交易量
	 */
	public double currency_volume;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KlineInfo)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		KlineInfo t = (KlineInfo) obj;
		return time == t.time && close == t.close
				&& open == t.open && highest == t.highest
				&& lowest == t.lowest;
	}

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
