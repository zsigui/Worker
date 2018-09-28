package sg.jackiez.worker.module.ok.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.callback.FutureDataChangedCallback;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.FutureHold4Fix;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.module.ok.model.account.FutureUserInfo;
import sg.jackiez.worker.module.ok.network.action.AccountRestApi;
import sg.jackiez.worker.module.ok.network.action.IAccountRestApi;
import sg.jackiez.worker.module.ok.network.future.IFutureRestApi;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class StrategyHandler extends DefaultThread implements FutureDataChangedCallback {

    BlockingQueue<Runnable> mTasks = new LinkedBlockingQueue<>(5);
    private boolean mIsRunning = false;
    private Object mLockObj = new Object();
    private Thread mGrabDataThread;
    private IAccountRestApi mAccountRestApi;
    private IFutureRestApi mFutureRestApi;

    private String mSymbol = "eos_usdt";
    private String mContractType = OKTypeConfig.CONTRACT_TYPE_QUARTER;

    public StrategyHandler() {
        super();
        setPriority(Thread.MAX_PRIORITY);
        start();
        mIsRunning = true;
    }


    private void startGrabOrderAndAccountThread() {
        mGrabDataThread = new DefaultThread(() -> {
            while (mIsRunning) {
                ArrayList<FutureHold4Fix> holdList = JsonUtil.jsonToSuccessDataForFuture(
                        mFutureRestApi.futurePositionForFix(mSymbol, mContractType),
                        "holding", new ArrayList<FutureHold4Fix>(){}.getClass());
                if (holdList != null) {
                    // 当前有持仓
                }
                FutureUserInfo userInfo = JsonUtil.jsonToSuccessDataForFuture(mFutureRestApi.futureUserInfoForFix(),
                        "info", FutureUserInfo.class);
                if (userInfo != null) {
                    // 获取账户信息成功
                }

                try {
                    wait(10_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mGrabDataThread.start();
    }


    @Override
    public void onDepthUpdated(List<DepthInfo> depthInfoList) {

    }

    @Override
    public void onKlineInfoUpdated(String shortTimeType, List<KlineInfo> shortKlineInfos,
                                   String longTimeType, List<KlineInfo> longKlineInfos) {

    }

    @Override
    public void onTickerDataUpdate(Ticker ticker) {
    }
}
