package sg.jackiez.worker.module.ok.model.account;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 逐仓合约账户指定币种持有合约内容详情类
 *
 * @Author JackieZ
 * @Date Created on 2018/9/29
 */
public class FutureContractDetail4FixV3 extends BaseM {

	/**
	 * 	逐仓可用余额
	 */
	public double available_qty;

	/**
	 * 逐仓账户余额
	 */
	public double fixed_balance;

	/**
	 * 合约ID
	 */
	public String instrument_id;

	/**
	 * 挂单冻结保证金
	 */
	public double margin_for_unfilled;

	/**
	 * 冻结的保证金(成交以后仓位所需的)
	 */
	public double margin_frozen;

	/**
	 * 已实现盈亏
	 */
	public double realized_pnl;

	/**
	 * 	未实现盈亏
	 */
	public double unrealized_pnl;
}
