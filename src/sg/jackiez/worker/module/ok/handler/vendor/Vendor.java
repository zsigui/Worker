package sg.jackiez.worker.module.ok.handler.vendor;

import com.fasterxml.jackson.core.type.TypeReference;

import sg.jackiez.worker.callback.SimpleCallback;
import sg.jackiez.worker.module.ok.OKError;
import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.module.ok.model.FutureTradeInfo;
import sg.jackiez.worker.module.ok.model.resp.RespCancelTradeV3;
import sg.jackiez.worker.module.ok.model.resp.RespTradeV3;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.module.ok.utils.ReqUtil;
import sg.jackiez.worker.utils.ExecutorUtil;
import sg.jackiez.worker.utils.SLogUtil;

public class Vendor {

    private static final String TAG = "Vendor";

    private static final int MAX_RETRY_TIME = 3;

    private RespTradeV3 doTradeInner(String instrumentId, double price, long size,
                                byte trendType, String priceType, String clientOId, String leverage) {
        SLogUtil.i(TAG, "执行下单操作：instrumentId = " + instrumentId
                + ", price = " + price + ", size = " + size + ", type = " + trendType
                + ", match_price = " + priceType + ", leverage = " + leverage + ", clientOId = " + clientOId);
        return JsonUtil.jsonToSuccessDataForFutureWithErrCode(FutureRestApiV3.doTrade(instrumentId,
                String.valueOf(trendType), String.valueOf(price),
                String.valueOf(size), priceType, leverage, clientOId),
                new TypeReference<RespTradeV3>() {});
    }

    public RespTradeV3 doTradeSync(FutureTradeInfo info) {
        DBManager.get().saveTrade(info, OKTypeConfig.DB_STATE_INIT);
        RespTradeV3 rsp = ReqUtil.retry(MAX_RETRY_TIME, () -> doTradeInner(info.instrumentId, info.price, info.amount, info.trendType,
                info.priceType, info.clientOId, String.valueOf(info.leverage)));
        if (rsp != null && rsp.result) {
            DBManager.get().updateTradeStateAndOrderId(info.clientOId, rsp.order_id, OKTypeConfig.DB_STATE_TRADING);
        }
        return rsp;
    }

    public void doTrade(FutureTradeInfo info, SimpleCallback<RespTradeV3> callback) {
        if (callback == null) {
            return;
        }

        ExecutorUtil.getCachedExecutor().submit(() -> {
            RespTradeV3 rsp = ReqUtil.retry(MAX_RETRY_TIME, () -> doTradeInner(info.instrumentId, info.price, info.amount, info.trendType,
                    info.priceType, info.clientOId, String.valueOf(info.leverage)));
            if (rsp == null){
                callback.onFail(OKError.E_NULL, OKError.STR_E_NULL);
            } else if (!rsp.result) {
                callback.onFail(OKError.E_EMPTY, OKError.STR_E_EMPTY);
            } else {
                DBManager.get().updateTradeStateAndOrderId(info.clientOId, rsp.order_id, OKTypeConfig.DB_STATE_TRADING);
                callback.onSuccess(rsp);
            }
        });
    }

    private RespCancelTradeV3 doCancelOrderInner(String instrumentId, String orderId) {
        SLogUtil.i(TAG, "执行取消下单操作：instrumentId = " + instrumentId + ", orderId = " + orderId);
        return JsonUtil.jsonToSuccessDataForFuture(FutureRestApiV3.doCancelTrade(orderId, instrumentId),
                new TypeReference<RespCancelTradeV3>() {});
    }

    public RespCancelTradeV3 doCancelOrderSync(String instrumentId, String orderId) {
        RespCancelTradeV3 rsp = ReqUtil.retry(MAX_RETRY_TIME, () -> doCancelOrderInner(instrumentId, orderId));
        if (rsp != null && rsp.result) {
            DBManager.get().updateTradeState(instrumentId, orderId, OKTypeConfig.DB_STATE_CANCELLING);
        }
        return rsp;
    }

    public void doCancelOrder(String instrumentId, String orderId,
                              SimpleCallback<RespCancelTradeV3> callback) {
        if (callback == null) {
            return;
        }

        ExecutorUtil.getCachedExecutor().submit(() -> {
            RespCancelTradeV3 rsp = ReqUtil.retry(MAX_RETRY_TIME, () -> doCancelOrderInner(instrumentId, orderId));
            if (rsp == null){
                callback.onFail(OKError.E_NULL, OKError.STR_E_NULL);
            } else if (!rsp.result) {
                callback.onFail(OKError.E_EMPTY, OKError.STR_E_EMPTY);
            } else {
                DBManager.get().updateTradeState(instrumentId, orderId, OKTypeConfig.DB_STATE_CANCELLING);
                callback.onSuccess(rsp);
            }
        });
    }
}
