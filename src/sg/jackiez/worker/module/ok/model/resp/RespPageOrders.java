package sg.jackiez.worker.module.ok.model.resp;

import java.util.List;

import sg.jackiez.worker.module.ok.model.Order;

/**
 * 获取历史订单信息分页类
 *
 * @Author JackieZ
 * @Date Created on 2018/9/9
 */
public class RespPageOrders extends RespPageData {

	public List<Order> orders;
}
