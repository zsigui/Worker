package sg.jackiez.worker.module.ok.model.account;

import java.util.List;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 逐仓合约账户指定币种信息类
 *
 * @Author JackieZ
 * @Date Created on 2018/9/29
 */
public class FutureContract extends BaseM {

	/**
	 * 账户余额
	 */
	public double balance;
	/**
	 * 持有的合约信息
	 */
	public List<FutureContract> contracts;
	/**
	 * 账户权益
	 */
	public double rights;
}
