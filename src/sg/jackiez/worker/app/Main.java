package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.OKHelper;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;

public class Main {

    public static void main(String[] args) {
        // write your code here


        OKHelper helper = OKHelper.get();

//        IStockRestApi stockRestApi = new StockRestApiV1();
//        RespTrade data = JsonUtil.jsonToSuccessDataForSpot("{\"order_id\":123456, \"error_code\":12333}",
//                RespTrade.class);

//        ArrayList<ReqTrade> reqTrades = new ArrayList<>();
//        ReqTrade trade = new ReqTrade();
//        trade.type = OKTypeConfig.TYPE_BUY_MARKET;
//        trade.price = 0.03;
//        trade.amount = 0.03;
//        reqTrades.add(trade);
        // 交易历史
//        ArrayList<TradeInfo> data = JsonUtil.jsonToSuccessDataForSpot(stockRestApi.tradeHistory("eos_usdt",
//                "460034278"), new ArrayList<TradeInfo>(){}.getClass());
//        SLogUtil.v(data);

//        List<KlineInfo> klineInfos = JsonUtil.jsonToKlineList(stockRestApi.kLine("eos_usdt",
//                OKTypeConfig.KLINE_TYPE_30_MIN, null, null));
//        List<List<Double>> rsiList = new RSI().calculateRSI(klineInfos);
//        List<List<Double>> kdjList = new KDJ().calculateKDJ(klineInfos);
//        List<List<Double>> macd = new MACD().calculateMACD(klineInfos);
//        SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
//        if (klineInfos != null) {
//            startTime = System.currentTimeMillis();
//            SLogUtil.v(klineInfos);
//            for (int i = 1; i<= 5; i++) {
//                SLogUtil.v("第" + i + "项:");
//                int lastIndex = klineInfos.size() - i;
//                SLogUtil.v("rsi6 =" + rsiList.get(0).get(lastIndex) + ", rsi12 = " + rsiList.get(1).get(lastIndex)
//                        + ", rsi24 = " + rsiList.get(2).get(lastIndex));
//                SLogUtil.v("k =" + kdjList.get(0).get(lastIndex) + ", d = " + kdjList.get(1).get(lastIndex) + ", j = " + kdjList.get(2).get(lastIndex));
//                SLogUtil.v("dif = " + macd.get(0).get(lastIndex) + ", dea = "
//                        + macd.get(1).get(lastIndex) + ", bar = " + macd.get(2).get(lastIndex));
//            }
//        }

//        SLogUtil.v(JsonUtil.jsonToSuccessDataForSpot(stockRestApi.orderHistory("eos_usdt",
//                "1", "1", "30"), RespPageOrders.class));
//        SLogUtil.i(JsonUtil.jsonToBackObj(stockRestApi.ticker("eos_usdt"),
//                RespTicker.class, ErrorItem.class));
//        AccountRestApi accountRestApi = new AccountRestApi();
//        JsonUtil.jsonToSuccessDataForSpot(accountRestApi.walletInfo(), ErrorItem.class);

        IFutureRestApi futureRestApi = new FutureRestApiV1();

//        int i = 0;
//        RespTicker ticker1, ticker2 = null;
//        long curTime, lastTime = System.currentTimeMillis();
//        int k = 0, totalTime = 0;
//        while (i++ < 100) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            ticker1 = JsonUtil.jsonToSuccessDataForFuture(futureRestApi.futureTicker("eos_usdt", OKTypeConfig.CONTRACT_TYPE_QUARTER),
//                    RespTicker.class);
//            DepthInfo depthInfo = JsonUtil.jsonToSuccessDataForFuture(futureRestApi.futureDepth("eos_usdt", OKTypeConfig.CONTRACT_TYPE_QUARTER),
//                    DepthInfo.class);
//            SLogUtil.v("深度数据：" + depthInfo);
//            if (ticker1 == null || ticker1.ticker == null) {
//                SLogUtil.v("当前获取不到正确行情数据!");
//                continue;
//            }
//            if (ticker2 == null || !CompareUtil.equal(ticker1.ticker, ticker2.ticker)) {
//                ticker2 = ticker1;
//                curTime = System.currentTimeMillis();
//                SLogUtil.d("间隔时间：" + (curTime -  lastTime) + "ms, 获取到新的行情数据：" + ticker1);
//                if (k++ != 0) {
//                    totalTime += (curTime - lastTime);
//                }
//                lastTime = curTime;
//            }
//        }
//        SLogUtil.v("获取行情平均间隔: " + (totalTime / k) + "ms");
//        String mSymbol = "eos_usdt";
//        long startTime = System.currentTimeMillis();
//        Map<String, FutureContract> userInfo = JsonUtil.jsonToSuccessDataForFuture(futureRestApi.futureUserInfoForFix(),
//                "info", new HashMap<String, FutureContract>(){}.getClass());
//        if (userInfo != null) {
//            // 获取账户信息成功
//            String s = mSymbol.substring(0, mSymbol.indexOf("_"));
//            FutureContract contract = userInfo.get(s);
//            SLogUtil.v("contract = " + contract.contracts);
//        }
//
//        SLogUtil.v("total spend time on main = " + (System.currentTimeMillis() - startTime) + " ms");
        FutureDataGrabber grabber = new FutureDataGrabber();
//        grabber.startTickerGrabThread();
        grabber.startKlineGrabThread();
    }

}
