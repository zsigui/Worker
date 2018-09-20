package sg.jackiez.worker.module.ok.model;

public class FutureOrder extends Order{

    /**
     * 合约名称
     */
    public String contract_name;
    /**
     * 手续费
     */
    public double fee;
    /**
     * 平均价格
     */
    public double price_avg;
    /**
     * 合约面值
     */
    public int unit_amount;
    /**
     * 杠杠倍数，默认10
     */
    public int lever_rate;
}
