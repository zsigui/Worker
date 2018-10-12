package sg.jackiez.worker.debug;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.module.ok.utils.Utils;
import sg.jackiez.worker.utils.SLogUtil;

/**
 * 模拟持有账户信息
 */
public class TestAccount {

    /**
     * 最大下注金额
     */
    public static final double MAX_MONEY = 5000;
    /**
     * 最大下单张数
     */
    public static final int MAX_PAGE = 2000;
    /**
     * 每一张的买入面值（EOS是10美元/张）
     */
    public static final double PAGE_VALUE = 10;
    /**
     * 下单数额占未下单总额的最大百分比
     */
    public static final double MAX_PERCENT = 0.2;
    /**
     * 最大亏损10%
     */
    public static final double MAX_LOSS_RATE = 0.1;
    /**
     * 最大亏损30%
     */
    public static final double TOTAL_LOSS_RATE_TO_STOP = 0.3;

    public static final int INIT_MONEY = 200;

    private static final String TAG = "TestAccount";

    /**
     * 当前持有的下单张数(开多)
     */
    private int mHoldUpPage;
    /**
     * 买入开多时的均价
     */
    private double mHoldUpPageValue;
    private double mHoldUpMoney;
    /**
     * 当前持有的下单张数(开空)
     */
    private int mHoldDownPage;
    /**
     * 买入开空时的均价
     */
    private double mHoldDownPageValue;
    private double mHoldDownMoney;
    /**
     * 初始持有金额为200USDT，看经过一段时间测试之后的结果
     */
    private double mHoldMoney;
    /**
     * 开仓倍率
     */
    private int mLeverRate = 20;
    /**
     * 吃单手续费
     */
    private double mTranslationFee = 0.0003 * mLeverRate;

    public TestAccount() {
        mHoldUpPage = 0;
        mHoldUpPageValue = 0;
        mHoldUpMoney = 0;
        mHoldDownPage = 0;
        mHoldDownPageValue = 0;
        mHoldDownMoney = 0;
        mHoldMoney = INIT_MONEY;
    }

    public List<ProfitObj> mProfitRateList = new ArrayList<>();

    public void shortBuyHalf(double eosToUsdt) {
        shortBuy(eosToUsdt, MAX_PERCENT / 2);
    }

    public void shortBuyAll(double eosToUsdt) {
        shortBuy(eosToUsdt, MAX_PERCENT);
    }

    public void longBuyHalf(double eosToUsdt) {
        longBuy(eosToUsdt, MAX_PERCENT / 2);
    }

    public void longBuyAll(double eosToUsdt) {
        longBuy(eosToUsdt, MAX_PERCENT);
    }

    /**
     * 按指定比例买入开空
     * @param eosToUsdt 买入eos价格
     */
    public void shortBuy(double eosToUsdt, double buyPercent) {
        double maxMoney = mHoldMoney * buyPercent;
        // 当前最大可下单金额
        maxMoney = maxMoney > MAX_MONEY ? MAX_MONEY : maxMoney;
        // 当前最大可下单数
        int maxPage = (int)(maxMoney * mLeverRate / PAGE_VALUE * (1 - mTranslationFee));
        maxPage = maxPage > MAX_PAGE ? MAX_PAGE : maxPage;

        mHoldDownPage += maxPage;
        mHoldDownPageValue = eosToUsdt;
        mHoldDownMoney = maxPage / (1 - mTranslationFee) * PAGE_VALUE / mLeverRate;
        mHoldMoney -= mHoldDownMoney;
    }

    /**
     * 全部卖出平空
     * @param eosToUsdt 当前Eos价格
     */
    public void shortSell(double eosToUsdt) {
        double money = Utils.getShortProfit(PAGE_VALUE, mHoldDownPageValue, eosToUsdt, mHoldDownPage)
                * eosToUsdt - Utils.getTranslationFee(PAGE_VALUE, mHoldDownPage, mTranslationFee);
        money = money > -mHoldDownMoney ? money : -mHoldDownMoney;

        ProfitObj profitObj = new ProfitObj();
        profitObj.amount = mHoldDownPage;
        profitObj.basePrice = mHoldDownPageValue;
        profitObj.closePrice = eosToUsdt;
        profitObj.profitRate = money / mHoldDownMoney;
        mProfitRateList.add(profitObj);
        SLogUtil.v(TAG, profitObj);

        mHoldDownPage = 0;
        mHoldDownPageValue = 0;
        mHoldMoney += money + mHoldDownMoney;
        mHoldDownMoney = 0;
    }

    /**
     * 按指定比例买入开多
     * @param eosToUsdt 买入eos价格
     */
    public void longBuy(double eosToUsdt, double buyPercent) {
        double maxMoney = mHoldMoney * buyPercent;
        // 当前最大可下单金额
        maxMoney = maxMoney > MAX_MONEY ? MAX_MONEY : maxMoney;
        // 当前最大可下单数
        int maxPage = (int)(maxMoney * mLeverRate / PAGE_VALUE * (1 - mTranslationFee));
        maxPage = maxPage > MAX_PAGE ? MAX_PAGE : maxPage;

        mHoldUpPage += maxPage;
        mHoldUpPageValue = eosToUsdt;
        mHoldUpMoney = maxPage / (1 - mTranslationFee) * PAGE_VALUE / mLeverRate;
        mHoldMoney -= mHoldUpMoney;
    }

    /**
     * 全部卖出平空
     * @param eosToUsdt 当前Eos价格
     */
    public void longSell(double eosToUsdt) {
        double money = Utils.getLongProfit(PAGE_VALUE, mHoldUpPageValue, eosToUsdt, mHoldUpPage)
                * eosToUsdt - Utils.getTranslationFee(PAGE_VALUE, mHoldUpPage, mTranslationFee);
        money = money > -mHoldUpMoney ? money : -mHoldUpMoney;

        ProfitObj profitObj = new ProfitObj();
        profitObj.amount = mHoldUpPage;
        profitObj.basePrice = mHoldUpPageValue;
        profitObj.closePrice = eosToUsdt;
        profitObj.profitRate = money / mHoldUpMoney;
        mProfitRateList.add(profitObj);
        SLogUtil.v(TAG, profitObj);

        mHoldUpPage = 0;
        mHoldUpPageValue = 0;
        mHoldMoney += money + mHoldUpMoney;
        mHoldUpMoney = 0;
    }

    public double getCurrentMoney() {
        return mHoldMoney;
    }

    public boolean isHoldContracts() {
        return mHoldUpPage + mHoldDownPage > 0;
    }

    public int getHoldUpPage() {
        return mHoldUpPage;
    }

    public double getHoldUpPageValue() {
        return mHoldUpPageValue;
    }

    public double getHoldUpMoney() {
        return mHoldUpMoney;
    }

    public int getHoldDownPage() {
        return mHoldDownPage;
    }

    public double getHoldDownPageValue() {
        return mHoldDownPageValue;
    }

    public double getHoldDownMoney() {
        return mHoldDownMoney;
    }

    public double getHoldMoney() {
        return mHoldMoney;
    }

    public int getLeverRate() {
        return mLeverRate;
    }
}
