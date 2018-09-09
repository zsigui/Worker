package sg.jackiez.worker.app;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.action.AccountRestApi;
import sg.jackiez.worker.module.ok.model.ErrorItem;
import sg.jackiez.worker.module.ok.model.Order;
import sg.jackiez.worker.module.ok.model.account.UserInfo;
import sg.jackiez.worker.module.ok.model.resp.RespTicker;
import sg.jackiez.worker.module.ok.stock.StockRestApi;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        // write your code here

//        OKHelper helper = new OKHelper();

        StockRestApi stockRestApi = new StockRestApi();
        List<Order> orderList = new ArrayList<>();
        SLogUtil.i(JsonUtil.jsonToSuccessData(stockRestApi.orderInfo(
                "usdt_eos", "-1"),
                "orders", orderList.getClass()));
        SLogUtil.i(JsonUtil.jsonToBackObj(stockRestApi.ticker("eos_usdt"),
                RespTicker.class, ErrorItem.class));
        AccountRestApi accountRestApi = new AccountRestApi();
        JsonUtil.jsonToSuccessData(accountRestApi.walletInfo(), ErrorItem.class);

    }

}
