package sg.jackiez.worker.module.ok.handler;

import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.resp.RespTicker;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.module.ok.utils.CompareUtil;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.SLogUtil;

public class FutureHandler {

    private IFutureRestApi mRestApi = new FutureRestApiV1();
    private FutureVendor mVendor;
    private String mSymbol;
    private String mContractType;

    private boolean mIsRunning = false;

    public FutureHandler() {
        mVendor = new FutureVendor(mRestApi, mContractType);
    }

    public void start() {
        RespTicker tickerNew, tickerOld = null;
        while (mIsRunning) {
            DepthInfo depthInfo = JsonUtil.jsonToSuccessDataForFuture(mRestApi.futureDepth(mSymbol, mContractType),
                    DepthInfo.class);


            tickerNew = JsonUtil.jsonToSuccessDataForFuture(mRestApi.futureTicker(mSymbol, mContractType),
                    RespTicker.class);
            if (tickerNew != null && tickerNew.ticker != null) {
                SLogUtil.v("当前获取不到正确行情数据!");
                if (tickerOld == null || !CompareUtil.equal(tickerNew.ticker, tickerOld.ticker)) {
                    tickerOld = tickerNew;
                }
            }
        }
    }

    public void stop() {
        mIsRunning = false;
    }

}
