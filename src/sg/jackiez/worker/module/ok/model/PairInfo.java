package sg.jackiez.worker.module.ok.model;

/**
 * 交易对信息
 */
public class PairInfo {

	/**
	 * 交易对Id
	 */
	public int product_id;

	/**
	 * 标志符号
	 */
	public String symbol;

	/**
	 * 最小交易量
	 */
	public double base_min_size;

	/**
	 * 交易货币精度
	 */
	public double base_increment;

	/**
	 * 计价货币精度
	 */
	public double quote_increment;
}
