package sg.jackiez.worker.module.ok.model;

/**
 * 逐仓用户持仓
 *
 * @Author JackieZ
 * @Date Created on 2018/9/28
 */
public class FutureHold4Fix extends FutureHold {

	/**
	 * 多仓保证金
	 */
	public double buy_bond;
	/**
	 * 多仓强平价格
	 */
	public double buy_flatprice;
	/**
	 * 多仓盈亏比
	 */
	public double buy_profit_lossratio;
	/**
	 * 空仓保证金
	 */
	public double sell_bond;
	/**
	 * 空仓强平价格
	 */
	public double sell_flatprice;
	/**
	 * 空仓盈亏比
	 */
	public double sell_profit_lossratio;
}
