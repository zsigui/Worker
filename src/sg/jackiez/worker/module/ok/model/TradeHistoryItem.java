package sg.jackiez.worker.module.ok.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import sg.jackiez.worker.module.ok.model.base.BaseM;
import sg.jackiez.worker.utils.DateUtil;

/**
 * 成交历史项(交易池里)
 *
 * @Author JackieZ
 * @Date Created on 2018/10/21
 */
public class TradeHistoryItem extends BaseM {

	/**
	 * ISO时间戳转换的时间
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
	public Date timestamp;
	/**
	 * 成交ID
	 */
	public long trade_id;
	/**
	 * 成交价格
	 */
	public double price;
	/**
	 * 成交数量/张
	 */
	public double qty;
	/**
	 * 成交方向 buy / sell
	 */
	public String side;
}
