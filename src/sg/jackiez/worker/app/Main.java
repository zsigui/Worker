package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.action.AccountRestApi;
import sg.jackiez.worker.module.ok.model.ErrorItem;
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
        SLogUtil.i(JsonUtil.jsonToSuccessData(stockRestApi.userInfo(),
                "info", UserInfo.class));
        SLogUtil.i(JsonUtil.jsonToBackObj(stockRestApi.ticker("eos_usdt"),
                RespTicker.class, ErrorItem.class));
        AccountRestApi accountRestApi = new AccountRestApi();
        JsonUtil.jsonToSuccessData(accountRestApi.walletInfo(), ErrorItem.class);

    }

}
