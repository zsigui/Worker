package sg.jackiez.worker.module.ok.handler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sg.jackiez.worker.module.ok.callback.FutureDataChangedCallback;
import sg.jackiez.worker.module.ok.model.DepthInfo;
import sg.jackiez.worker.module.ok.model.Ticker;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.thread.DefaultThread;

public class StrategyHandler extends DefaultThread implements FutureDataChangedCallback {

    BlockingQueue<Runnable> mTasks = new LinkedBlockingQueue<>(5);
    private boolean mIsRunning = false;

    public StrategyHandler() {
        super();
        setPriority(Thread.MAX_PRIORITY);
        start();
        mIsRunning = true;
    }


    private void startGrabOrderAndAccountThread() {
        Thread t = new DefaultThread(() -> {

        });
        t.start();
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
