package sg.jackiez.worker.module.ok.model.resp;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/30
 */
public class RespTradeV3 {

	public boolean result;

	/**
	 * 订单ID，下单失败时，此字段值为-1
	 */
	public String order_id;

	/**
	 * 错误码
	 */
	public int error_code;

	/**
	 * 错误消息
	 */
	public String error_message;

	/**
	 * 	由自己设置的订单ID来识别您的订单
	 */
	public String client_oid;
}
