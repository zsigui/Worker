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
	                                      double contractValue,
										  double feeRate, int leverage) {
		return (int) (amount * price * (1 - feeRate * leverage) / contractValue);
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

	/**
	 * 获取当前合约的保证金率
	 *
	 * @param fixProtectionFee 固定保证金
	 * @param unProfit 未实现权益
	 * @param openPrice 开仓均价
	 * @param leverRate 倍率
	 * @param pageValue 合约面值
	 * @param amount 合约数量
	 * @return
	 */
	public static double getProtectionRate(double fixProtectionFee,
	                                       double unProfit,
	                                       double openPrice,
	                                       int leverRate,
	                                       double pageValue,
	                                       int amount) {
		return (fixProtectionFee + unProfit) * openPrice * leverRate / (pageValue * amount);
	}

	/**
	 * 根据下单精度获取下单数量
	 */
	public static double getCountByIncrement(double val, double trade_increment) {
		return (int)(val / trade_increment) * trade_increment;
	}

}
