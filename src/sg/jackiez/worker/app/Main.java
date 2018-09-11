package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.module.ok.model.resp.RespPageOrders;
import sg.jackiez.worker.module.ok.stock.IStockRestApi;
import sg.jackiez.worker.module.ok.stock.StockRestApi;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        // write your code here

        long startTime = System.currentTimeMillis();
//        OKHelper helper = new OKHelper();

        IStockRestApi stockRestApi = new StockRestApi();
//        RespTrade data = JsonUtil.jsonToSuccessData("{\"order_id\":123456, \"error_code\":12333}",
//                RespTrade.class);

        OKHelper.get();
//        ArrayList<ReqTrade> reqTrades = new ArrayList<>();
//        ReqTrade trade = new ReqTrade();
//        trade.type = OKTypeConfig.TYPE_BUY_MARKET;
//        trade.price = 0.03;
//        trade.amount = 0.03;
//        reqTrades.add(trade);
//        RespBatchTrade data = JsonUtil.jsonToSuccessData(stockRestApi.batchTrade("eos_usdt",
//                null, JsonUtil.objToJson(reqTrades)),
//                RespBatchTrade.class);
//        SLogUtil.v(data);
        SLogUtil.v(JsonUtil.jsonToSuccessData(stockRestApi.orderHistory("eos_usdt",
                "1", "1", "30"), RespPageOrders.class));
//        SLogUtil.i(JsonUtil.jsonToBackObj(stockRestApi.ticker("eos_usdt"),
//                RespTicker.class, ErrorItem.class));
//        AccountRestApi accountRestApi = new AccountRestApi();
//        JsonUtil.jsonToSuccessData(accountRestApi.walletInfo(), ErrorItem.class);

        SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
    }

}
