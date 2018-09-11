package sg.jackiez.worker.utils.thread;

import sg.jackiez.worker.utils.SLogUtil;

public class DefaultThread extends Thread{

    public DefaultThread() {
        super();
    }

    public DefaultThread(Runnable target) {
        super(target);
    }

    public DefaultThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public DefaultThread(String name) {
        super(name);
    }

    public DefaultThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public DefaultThread(Runnable target, String name) {
        super(target, name);
    }

    public DefaultThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public DefaultThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        super.run();
        SLogUtil.d(getName(), "runnable total spend time : " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
