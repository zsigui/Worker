package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.module.ok.OKTypeConfig;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/20
 */
public class TradeInfoV3 extends TradeInfo {

	/**
	 * 	是否以对手价下单(0:不是 1:是)，默认为0，当取值为1时。price字段无效
	 */
	public String match_price = OKTypeConfig.PRICE_TYPE_PARTILY_PRICE;
}
