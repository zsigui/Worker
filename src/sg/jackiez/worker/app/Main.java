package sg.jackiez.worker.app;

import java.lang.reflect.Field;

import sg.jackiez.worker.module.ok.model.ErrorItem;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        // write your code here

//        StockRestApi stockRestApi = new StockRestApi();
////        SLogUtil.i(HttpManager.get().doGet("https://www.okex.com/api/v1/tickers.do", null));
//        SLogUtil.i(stockRestApi.userInfo());
////        SLogUtil.i(stockRestApi.ticker("eos_usdt"));
////
//        AccountRestApi accountRestApi = new AccountRestApi();
//        SLogUtil.i(accountRestApi.walletInfo());

        Object obj = new ErrorItem();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            SLogUtil.e(f.getName() + ", " + f.getAnnotatedType());
        }
    }
}
