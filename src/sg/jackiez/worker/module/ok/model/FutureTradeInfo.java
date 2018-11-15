package sg.jackiez.worker.module.ok.model;

import java.util.List;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class FutureTradeInfo extends BaseM {

    // 进行交易记录
    public String instrumentId;
    public double price;
    public long amount;
    public byte trendType;
    public String priceType;
    public String clientOId;
    public int leverage;

    // 取消订单记录
    public String orderId;
    public boolean isCancelOp;
    public List<String> orderIds;

    public FutureTradeInfo(String instrumentId, double price, long amount, byte trendType, String priceType, int leverage) {
        this.instrumentId = instrumentId;
        this.price = price;
        this.amount = amount;
        this.trendType = trendType;
        this.priceType = priceType;
        this.leverage = leverage;
        this.isCancelOp = false;
    }

    public FutureTradeInfo(String instrumentId, String orderId) {
        this.instrumentId = instrumentId;
        this.orderId = orderId;
        this.isCancelOp = true;
    }

    public FutureTradeInfo(String instrumentId, List<String> orderIds) {
        this.instrumentId = instrumentId;
        this.orderIds = orderIds;
        this.isCancelOp = true;
    }

}
