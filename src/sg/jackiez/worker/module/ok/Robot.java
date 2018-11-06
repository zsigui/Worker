package sg.jackiez.worker.module.ok;

import java.util.ArrayList;
import java.util.List;

import sg.jackiez.worker.module.ok.callback.AccountStateChangeCallback;
import sg.jackiez.worker.module.ok.callback.CallbackManager;
import sg.jackiez.worker.module.ok.callback.FutureDataChangeCallback;
import sg.jackiez.worker.module.ok.callback.VendorResultCallback;
import sg.jackiez.worker.module.ok.handler.AccountDataGrabber;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.handler.vendor.FutureVendorV3;
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
    private FutureVendorV3 mFutureVendor;

    private IPerformance mPerformance;
    private boolean mIsDataChange;

    private AccountStateChangeCallback mStateChangeCallback = new AccountStateChangeCallback() {
        @Override
        public void onAccountInfoUpdated() {
            if (mIsDataChange) {
//                handleKlineForSignal(mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN));
            }
        }

        @Override
        public void onAccountInfoOutdated() {

        }
    };

    private FutureDataChangeCallback mDataChangeCallback = new FutureDataChangeCallback() {
        @Override
        public void onDepthUpdated(DepthInfo depthInfo) {
            KlineInfo klineInfo = new KlineInfo();
            klineInfo.time = System.currentTimeMillis();
            klineInfo.close = (depthInfo.asks.get(depthInfo.asks.size() - 1).get(0) +
                    + depthInfo.bids.get(0).get(0)) / 2;
            List<KlineInfo> klineInfos = new ArrayList<>(mFutureDataGrabber.getKlineInfoMap().get(OKTypeConfig.KLINE_TYPE_1_MIN));
            klineInfos.add(klineInfo);
//            handleKlineForSignal(klineInfos);
        }

        @Override
        public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos, String longTimeType, List<KlineInfo> longKlineInfos) {
//            handleKlineForSignal(shortKlineInfos);
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

        }
    };

    private VendorResultCallback mVendorResultCallback = new VendorResultCallback() {
        @Override
        public void onTradeSuccess(String clientOId, String orderId, String instrumentId) {

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
        mFutureDataGrabber = new FutureDataGrabber(precursorManager.getInstrumentId());
        mAccountDataGrabber = new AccountDataGrabber();
        mFutureVendor = new FutureVendorV3(precursorManager.getLongLeverage(),
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
