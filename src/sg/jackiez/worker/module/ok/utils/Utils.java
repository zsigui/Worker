package sg.jackiez.worker.module.ok.utils;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/3
 */
public class Utils {

	/**
	 * 计算合约可下张数
	 */
	public static int calculatePageAmount(double amount,
	                                      double price,
	                                      double contractValue) {
		return (int) (amount * price / contractValue);
	}

	/**
	 * 获取多仓当前价格盈亏
	 *
	 * @param contractValue  合约面值, BTC 100美元, 其他 10美元
	 * @param closeBasePrice 结算基准价
	 * @param currentPrice   当前价格
	 * @param closeAmount    平仓数量
	 */
	public static double getLongProfit(double contractValue,
	                                   double closeBasePrice,
	                                   double currentPrice,
	                                   double closeAmount) {
		return (contractValue / closeBasePrice - contractValue / currentPrice) * closeAmount;
	}

	/**
	 * 获取空仓当前价格盈亏
	 *
	 * @param contractValue  合约面值, BTC 100美元, 其他 10美元
	 * @param closeBasePrice 结算基准价
	 * @param currentPrice   平仓价格
	 * @param closeAmount    平仓数量
	 */
	public static double getShortProfit(double contractValue,
	                                    double closeBasePrice,
	                                    double currentPrice,
	                                    double closeAmount) {
		return (contractValue / currentPrice - contractValue / closeBasePrice) * closeAmount;
	}

	/**
	 * 获取回本的
	 * @param makerPoundage
	 * @param takerPoundage
	 * @return
	 */
	public static double getProfitPosition(double makerPoundage,
	                                       double takerPoundage) {
		return 1 / ((1 - makerPoundage) * (1 - takerPoundage));
	}
}
