package sg.jackiez.worker.app;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.callback.NoParamCallback;
import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.model.resp.RespPageOrders;
import sg.jackiez.worker.module.ok.stock.IStockRestApi;
import sg.jackiez.worker.module.ok.stock.StockRestApi;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.module.util.UniversalDataSource;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.ThreadUtil;
import sg.jackiez.worker.utils.algorithm.KDJ;
import sg.jackiez.worker.utils.algorithm.MACD;
import sg.jackiez.worker.utils.algorithm.RSI;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

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
//        SLogUtil.v(JsonUtil.jsonToSuccessData(stockRestApi.orderHistory("eos_usdt",
//                "1", "1", "30"), RespPageOrders.class));
//	    SLogUtil.v(JsonUtil.jsonToSuccessData(stockRestApi.kLine("eos_usdt",
//			    OKTypeConfig.KLINE_TYPE_1_HOUR, "200", null), new ArrayList<ArrayList<Double>>(){}.getClass()));
////        SLogUtil.i(JsonUtil.jsonToBackObj(stockRestApi.ticker("eos_usdt"),
////                RespTicker.class, ErrorItem.class));
////        AccountRestApi accountRestApi = new AccountRestApi();
////        JsonUtil.jsonToSuccessData(accountRestApi.walletInfo(), ErrorItem.class);
//
//        SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
//        UniversalDataSource.get().refreshRateData(new NoParamCallback() {
//            @Override
//            public void onSuccess() {
//                SLogUtil.v(UniversalDataSource.get().getCnyToUsd());
//            }
//
//            @Override
//            public void onFail() {
//
//            }
//        });

	    List<KlineInfo> klineInfos = JsonUtil.jsonToKlinkData(stockRestApi.kLine("eos_usdt",
			    OKTypeConfig.KLINE_TYPE_30_MIN, "1000", null));
	    List<Double> rsiList = new RSI().calculateRSI(klineInfos);
	    List<List<Double>> kdjList = new KDJ().calculateKDJ(klineInfos);
	    List<List<Double>> macd = new MACD().calculateMACD(klineInfos);
	    SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
	    SLogUtil.v(klineInfos);
	    int lastIndex = klineInfos.size() - 1;
	    SLogUtil.v(rsiList.get(lastIndex));
	    SLogUtil.v("k =" + kdjList.get(0).get(lastIndex) + ", d = " + kdjList.get(1).get(lastIndex) + ", j = " + kdjList.get(2).get(lastIndex));
	    SLogUtil.v("dif = " + macd.get(0).get(lastIndex) + ", dea = "
	     +macd.get(1).get(lastIndex)+ ", bar = " + macd.get(2).get(lastIndex));
	    SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
    }

}
