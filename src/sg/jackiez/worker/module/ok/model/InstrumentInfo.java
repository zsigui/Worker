package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 合约信息类
 *
 * @Author JackieZ
 * @Date Created on 2018/10/20
 */
public class InstrumentInfo extends BaseM {

	/**
	 * 	合约ID，如BTC-USD-180213
	 */
	public String instrument_id;

	/**
	 * 	交易货币币种，如：btc-usd中的btc
	 */
	public String underlying_index;

	/**
	 * 计价货币币种，如：btc-usd中的usd
	 */
	public String quote_currency;

	/**
	 * 下单价格精度
	 */
	public double tick_size;

	/**
	 * 合约面值(美元)
	 */
	public double contract_val;

	/**
	 * 上线日期
	 */
	public String listing;

	/**
	 * 交割日期
	 */
	public String delivery;

	/**
	 * 下单数量精度
	 */
	public double trade_increment;
}
