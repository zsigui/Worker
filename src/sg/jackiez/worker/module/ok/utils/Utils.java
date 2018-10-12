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
	 * @return 盈利数量
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
	 * @return 盈利数量
	 */
	public static double getShortProfit(double contractValue,
	                                    double closeBasePrice,
	                                    double currentPrice,
	                                    double closeAmount) {
		return (contractValue / currentPrice - contractValue / closeBasePrice) * closeAmount;
	}

	/**
	 * 计算交易的手续费(经询问开仓跟平仓都是一样的手续费)
	 *
	 * @param contractValue 合约面值/开仓均价
	 * @param amount 张数/开仓数量
	 * @param feeRate 手续费率
	 */
	public static double getTranslationFee(double contractValue,
										   double amount,
										   double feeRate) {
		return contractValue * amount * feeRate;
	}
}
