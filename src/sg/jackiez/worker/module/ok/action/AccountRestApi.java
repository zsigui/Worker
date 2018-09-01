package sg.jackiez.worker.module.ok.action;

import java.util.Map;

import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.utils.common.CollectionUtil;
import sg.jackiez.worker.utils.http.HttpManager;
import sg.jackiez.worker.utils.http.HttpUtil;

import static sg.jackiez.worker.module.ok.OkConfig.KEY_API_KEY;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_FROM;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SIGN;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_SYMBOL;
import static sg.jackiez.worker.module.ok.OkConfig.KEY_TO;

public class AccountRestApi implements IAccountRestApi{

    private HttpManager mHttpManager = HttpManager.get();

    @Override
    public String fundsTransfer(String symbol, String amount, int from, int to) {
        Map<String, String> params = CollectionUtil.getExtraMap(
                KEY_API_KEY, OkConfig.API_KEY,
                KEY_SYMBOL, symbol,
                KEY_FROM, Integer.toString(from),
                KEY_TO, Integer.toString(to)
        );
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Account.FUNDS_TRANSFER_URL, params);
    }

    @Override
    public String walletInfo() {
        Map<String, String> params = CollectionUtil.getExtraMap(KEY_API_KEY, OkConfig.API_KEY);
        String sign = HttpUtil.createOkSignByParam(params, OkConfig.SECRET_KEY);
        params.put(KEY_SIGN, sign);

        return mHttpManager.doPost(OkConfig.Account.WALLET_INFO, params);
    }
}
