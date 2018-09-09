package sg.jackiez.worker.module.ok.model.resp;

/**
 * @Author JackieZ
 * @Date Created on 2018/9/9
 */
abstract class RespPageData {

	/**
	 * 返回结果
	 */
	public boolean result;

	/**
	 * 当前页码
	 */
	public int current_page;

	/**
	 * 每页数据条数
	 */
	public int page_length;

	/**
	 * 总的数据条数
	 */
	public int total;
}
