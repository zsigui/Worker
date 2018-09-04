package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.action.AccountRestApi;
import sg.jackiez.worker.module.ok.stock.StockRestApi;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        // write your code here

//        OKHelper helper = new OKHelper();

        StockRestApi stockRestApi = new StockRestApi();
//        SLogUtil.i(HttpManager.get().doGet("https://www.okex.com/api/v1/tickers.do", null));
        SLogUtil.i(stockRestApi.userInfo());
//        SLogUtil.i(stockRestApi.ticker("eos_usdt"));
//
        AccountRestApi accountRestApi = new AccountRestApi();
        SLogUtil.i(accountRestApi.walletInfo());

    }

}
