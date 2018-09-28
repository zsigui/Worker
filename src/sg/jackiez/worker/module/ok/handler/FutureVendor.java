package sg.jackiez.worker.module.ok.handler;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;

/**
 * 用于进行期货交易及现有资产管理的处理类
 */
public class FutureVendor implements IVendor{

    private IFutureRestApi mRestApi;
    private String mCurContractType;
    private String mLeverRate;

    public FutureVendor(IFutureRestApi restApi) {
        this(restApi, OKTypeConfig.CONTRACT_TYPE_QUARTER, OKTypeConfig.LEVER_RATE_20);
    }

    public FutureVendor(IFutureRestApi restApi, String curContractType, String leverRate) {
        mRestApi = restApi;
        mCurContractType = curContractType;
        mLeverRate = leverRate;
    }

    public void setCurContractType(String curContractType) {
        mCurContractType = curContractType;
    }

    private void doTrade(String symbol, double price, double amount,
                         byte trendType, String priceType) {
        mRestApi.futureTrade(symbol, mCurContractType, price == 0 ? null : String.valueOf(price),
                String.valueOf(amount), String.valueOf(trendType), priceType, mLeverRate);
    }

    public void buyShort(String symbol, double price, double amount) {
        doTrade(symbol, price, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE);
    }

    public void buyShortDirectly(String symbol, double amount) {
        doTrade(symbol, 0, amount, OKTypeConfig.TREND_TYPE_BUY_SHORT,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE);
    }

    public void sellShort(String symbol, double price, double amount) {
        doTrade(symbol, price, amount, OKTypeConfig.TREND_TYPE_SELL_SHORT,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE);
    }

    public void sellShortDirectly(String symbol, double amount) {
        doTrade(symbol, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE);
    }

    public void buyLong(String symbol, double price, double amount) {
        doTrade(symbol, price, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE);
    }

    public void buyLongDirectly(String symbol, double amount) {
        doTrade(symbol, 0, amount, OKTypeConfig.TREND_TYPE_BUY_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE);
    }

    public void sellLong(String symbol, double price, double amount) {
        doTrade(symbol, price, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_PARTILY_PRICE);
    }

    public void sellLongDirectly(String symbol, double amount) {
        doTrade(symbol, 0, amount, OKTypeConfig.TREND_TYPE_SELL_LONG,
                OKTypeConfig.PRICE_TYPE_MARKET_PRICE);
    }

}
