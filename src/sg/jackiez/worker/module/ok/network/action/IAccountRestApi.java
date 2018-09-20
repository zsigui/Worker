package sg.jackiez.worker.module.ok.network.action;

import sg.jackiez.worker.utils.annotations.NonNull;

/**
 * 用户账户相关 REST API
 */
public interface IAccountRestApi {

    String fundsTransfer(@NonNull String symbol, @NonNull String amount,
                         int from, int to);

    String walletInfo();

    /**
     * 个人资金划转，币币和合约互转
     * @param symbol 币对 btc_usd ltc_usd eth_usd etc_usd bch_usd
     * @param type 划转类型。1：币币转合约 2：合约转币币
     * @param amount 划转币的数量
     */
    String devolveFutureAndSpot(@NonNull String symbol, @NonNull String type, @NonNull String amount);


}
