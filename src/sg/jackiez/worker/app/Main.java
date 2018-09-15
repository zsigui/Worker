package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.module.ok.model.PairInfo;
import sg.jackiez.worker.module.ok.model.resp.RespTicker;
import sg.jackiez.worker.module.ok.stock.IStockRestApi;
import sg.jackiez.worker.module.ok.stock.StockRestApi;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        // write your code here

        long startTime = System.currentTimeMillis();
        OKHelper helper = OKHelper.get();

        IStockRestApi stockRestApi = new StockRestApi();
//        RespTrade data = JsonUtil.jsonToSuccessData("{\"order_id\":123456, \"error_code\":12333}",
//                RespTrade.class);

//        ArrayList<ReqTrade> reqTrades = new ArrayList<>();
//        ReqTrade trade = new ReqTrade();
//        trade.type = OKTypeConfig.TYPE_BUY_MARKET;
//        trade.price = 0.03;
//        trade.amount = 0.03;
//        reqTrades.add(trade);
        // 交易历史
//        ArrayList<TradeInfo> data = JsonUtil.jsonToSuccessData(stockRestApi.tradeHistory("eos_usdt",
//                "460034278"), new ArrayList<TradeInfo>(){}.getClass());
//        SLogUtil.v(data);

        PairInfo eosInfo = helper.findPairInfo("eos_usdt");
        SLogUtil.v("eos pair info : " + eosInfo);
        if (eosInfo != null) {
            while (true) {
                // 间隔500ms获取一次
//                List<KlineInfo> klineData = JsonUtil.jsonToKlineList(stockRestApi.kLine("eos_usdt",
//                        OKTypeConfig.KLINE_TYPE_1_MIN, null, null));
//                ArrayList<ArrayList<Double>> klineData = JsonUtil.jsonToSuccessData(stockRestApi.kLine("eos_usdt",
//                        OKTypeConfig.KLINE_TYPE_1_MIN, null, null), new ArrayList<ArrayList<Double>>(){}.getClass());

                SLogUtil.v(JsonUtil.jsonToSuccessData(stockRestApi.ticker("eos_usdt"),
                        RespTicker.class));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }

//        SLogUtil.v(JsonUtil.jsonToSuccessData(stockRestApi.orderHistory("eos_usdt",
//                "1", "1", "30"), RespPageOrders.class));
//        SLogUtil.i(JsonUtil.jsonToBackObj(stockRestApi.ticker("eos_usdt"),
//                RespTicker.class, ErrorItem.class));
//        AccountRestApi accountRestApi = new AccountRestApi();
//        JsonUtil.jsonToSuccessData(accountRestApi.walletInfo(), ErrorItem.class);

        SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
    }

}
