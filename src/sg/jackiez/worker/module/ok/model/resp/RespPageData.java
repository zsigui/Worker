package sg.jackiez.worker.module.ok.model.resp;

import sg.jackiez.worker.module.ok.model.base.BaseM;

/**
 * @Author JackieZ
 * @Date Created on 2018/9/9
 */
abstract class RespPageData extends BaseM {

	/**
	 * 返回结果
	 */
	public boolean result;

	/**
	 * 当前页码
	 */
	public int currency_page;

	/**
	 * 每页数据条数
	 */
	public int page_length;

	/**
	 * 总的数据条数
	 */
	public int total;
}
