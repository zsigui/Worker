package sg.jackiez.worker.module.ok.model.resp;

import java.util.List;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * 批量下单结果
 *
 * @Author JackieZ
 * @Date Created on 2018/9/9
 */
public class RespBatchTrade extends BaseM {

	public boolean result;

	/**
	 * 每一单返回结果列表
	 */
	public List<RespTrade> order_info;

}
