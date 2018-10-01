package sg.jackiez.worker.module.ok.model.account;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 逐仓合约账户指定币种持有合约内容详情类
 *
 * @Author JackieZ
 * @Date Created on 2018/9/29
 */
public class FutureContractDetail extends BaseM {

	/**
	 * 合约可用
	 */
	public double available;
	/**
	 * 账户(合约)余额
	 */
	public double balance;
	/**
	 * 固定保证金
	 */
	public double bond;
	/**
	 * 合约ID
	 */
	public long contract_id;
	/**
	 * 合约类别
	 */
	public String contract_type;
	/**
	 * 冻结
	 */
	public double freeze;
	/**
	 * 已实现盈亏
	 */
	public double profit;
	/**
	 * 未实现盈亏
	 */
	public double unprofit;
}
