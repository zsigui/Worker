package sg.jackiez.worker.app;

import java.util.List;

import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.stock.IStockRestApi;
import sg.jackiez.worker.module.ok.stock.StockRestApi;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.KDJ;
import sg.jackiez.worker.utils.algorithm.MACD;
import sg.jackiez.worker.utils.algorithm.RSI;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

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

        List<KlineInfo> klineInfos = JsonUtil.jsonToKlineList(stockRestApi.kLine("eos_usdt",
                OKTypeConfig.KLINE_TYPE_30_MIN, null, null));
        List<List<Double>> rsiList = new RSI().calculateRSI(klineInfos);
        List<List<Double>> kdjList = new KDJ().calculateKDJ(klineInfos);
        List<List<Double>> macd = new MACD().calculateMACD(klineInfos);
        SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
        if (klineInfos != null) {
            startTime = System.currentTimeMillis();
            SLogUtil.v(klineInfos);
            for (int i = 1; i<= 5; i++) {
                SLogUtil.v("第" + i + "项:");
                int lastIndex = klineInfos.size() - i;
                SLogUtil.v("rsi6 =" + rsiList.get(0).get(lastIndex) + ", rsi12 = " + rsiList.get(1).get(lastIndex)
                        + ", rsi24 = " + rsiList.get(2).get(lastIndex));
                SLogUtil.v("k =" + kdjList.get(0).get(lastIndex) + ", d = " + kdjList.get(1).get(lastIndex) + ", j = " + kdjList.get(2).get(lastIndex));
                SLogUtil.v("dif = " + macd.get(0).get(lastIndex) + ", dea = "
                        + macd.get(1).get(lastIndex) + ", bar = " + macd.get(2).get(lastIndex));
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
