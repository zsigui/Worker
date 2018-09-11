package sg.jackiez.worker.utils;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import sg.jackiez.worker.utils.thread.DefaultDelayedRunnable;
import sg.jackiez.worker.utils.thread.DefaultThreadFactory;

public class ThreadUtil {

    private static ThreadPoolExecutor sPoolExecutor = new ThreadPoolExecutor(
            4, 8, 1, TimeUnit.MINUTES,
            new PriorityBlockingQueue<>(), DefaultThreadFactory.newInstance()
    );

    public static void post(Runnable task) {
        sPoolExecutor.execute(task);
    }

    public static void postAtDelay(Runnable task, long delayInMillis) {
        post(new DefaultDelayedRunnable(task, delayInMillis));
    }

    public static void remove(Runnable task) {
        sPoolExecutor.remove(task);
    }
}
