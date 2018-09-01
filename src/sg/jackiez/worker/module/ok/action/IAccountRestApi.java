package sg.jackiez.worker.module.ok.action;

import sg.jackiez.worker.utils.annotations.NonNull;

/**
 * 用户账户相关 REST API
 */
public interface IAccountRestApi {

    String fundsTransfer(@NonNull String symbol, @NonNull String amount,
                         int from, int to);

    String walletInfo();
}
