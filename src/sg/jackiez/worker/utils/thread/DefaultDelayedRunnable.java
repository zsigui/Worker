package sg.jackiez.worker.utils.thread;

import sg.jackiez.worker.utils.SLogUtil;

public class DefaultDelayedRunnable implements Runnable {

    private Runnable mProxyRunnable;
    private long mDelayInMillis;

    public DefaultDelayedRunnable(Runnable proxyRunnable, long delayInMillis) {
        mProxyRunnable = proxyRunnable;
        mDelayInMillis = delayInMillis;
    }

    @Override
    public void run() {
        if (mDelayInMillis > 0) {
            try {
                Thread.sleep(mDelayInMillis);
            } catch (InterruptedException ignored) {
            }
        }
        long startTime = System.currentTimeMillis();
        mProxyRunnable.run();
        SLogUtil.d("DefaultDelayedRunnable", "runnable total spend time : " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
