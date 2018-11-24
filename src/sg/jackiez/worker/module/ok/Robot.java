package sg.jackiez.worker.module.ok;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.module.ok.callback.AccountStateChangeCallback;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.FutureDataChangeCallback;
import sg.jackiez.worker.module.ok.callback.VendorResultCallback;
import sg.jackiez.worker.module.ok.handler.AccountDataGrabber;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.handler.vendor.VendorDataHandler;
import sg.jackiez.worker.module.ok.manager.PrecursorManager;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.module.ok.performance.IPerformance;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class Robot {

    private AccountDataGrabber mAccountDataGrabber;
    private FutureDataGrabber mFutureDataGrabber;
    private VendorDataHandler mFutureVendor;

    private IPerformance mPerformance;
    private boolean mIsDataChange;

    private AccountStateChangeCallback mStateChangeCallback = new AccountStateChangeCallback() {
        @Override
        public void onAccountInfoUpdated() {

        }

        @Override
        public void onAccountInfoOutdated() {

        }
    };

    private FutureDataChangeCallback mDataChangeCallback = new FutureDataChangeCallback() {
        @Override
        public void onDepthUpdated(DepthInfo depthInfo) {

        }

        @Override
        public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos, String longTimeType, List<KlineInfo> longKlineInfos) {

        }

        @Override
        public void onGetUpdatedKlineInfo(String timeType, List<KlineInfo> updated) {

        }

        @Override
        public void onTickerDataUpdate(Ticker ticker) {
            // 暂时忽略这个的处理
        }

        @Override
        public void onGetTradeHistory(List<TradeHistoryItem> tradeHistory) {
            mPerformance.handleBar(tradeHistory.get(tradeHistory.size() - 1));
            double totalPrice = 0;
            for (TradeHistoryItem item : tradeHistory) {
                totalPrice += item.price;
            }
            double curAvgPrice = totalPrice / tradeHistory.size();
            KlineInfo klineInfo = new KlineInfo();
            klineInfo.close = curAvgPrice;
            List<KlineInfo> klineInfos = new ArrayList<>();
            klineInfos.add(klineInfo);
            klineInfos.addAll(mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN));
        }
    };

    private VendorResultCallback mVendorResultCallback = new VendorResultCallback() {
        @Override
        public void onTradeSuccess(String clientOId, String orderId, String instrumentId) {
            mPerformance.afterTrade();
        }

        @Override
        public void onTradeFail(int errCode, String errMsg) {

        }

        @Override
        public void onCancelOrderSuccess(String orderId, String instrumentId) {

        }

        @Override
        public void onCancelOrderFail(int errCode, String errMsg) {

        }
    };


    public void start() {
        SLogUtil.setPrintFile(true);
        PrecursorManager precursorManager = PrecursorManager.get();
        precursorManager.init(OKTypeConfig.SYMBOL_EOS, OKTypeConfig.CONTRACT_TYPE_QUARTER);
        mPerformance.init();
        mFutureDataGrabber = new FutureDataGrabber(precursorManager.getInstrumentId());
        mAccountDataGrabber = new AccountDataGrabber();
        mFutureVendor = new VendorDataHandler(precursorManager.getLongLeverage(),
                precursorManager.getShortLeverage());
        mAccountDataGrabber.startGrabAccountDataThread();
        mFutureDataGrabber.startKlineGrabThread();
        mFutureDataGrabber.startTradeGrabThread();
        mFutureVendor.startTradeThread();
        CallbackManager.get().addAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().addFutureDataChangeCallback(mDataChangeCallback);
        CallbackManager.get().addVendorResultCallback(mVendorResultCallback);
    }

    public void stop() {
        mAccountDataGrabber.stopGrabAccountDataThread();
        mFutureDataGrabber.stopAll();
        mFutureVendor.stopTradeThread();
        CallbackManager.get().removeAccountStateChangeCallback(mStateChangeCallback);
        CallbackManager.get().removeFutureDataChangeCallback(mDataChangeCallback);
        CallbackManager.get().removeVendorResultCallback(mVendorResultCallback);
    }
}
