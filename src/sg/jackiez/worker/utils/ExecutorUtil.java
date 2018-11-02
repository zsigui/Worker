package sg.jackiez.worker.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.jackiez.worker.utils.thread.DefaultThread;

public class ExecutorUtil {

    private static volatile ExecutorService sSingleExecutor;
    private static volatile ExecutorService sCachedExecutor;
    private static volatile ExecutorService sFixedExecutor;

    public static ExecutorService getSingleExecutor() {
        if (sSingleExecutor == null || sSingleExecutor.isShutdown()) {
            sSingleExecutor = Executors.newSingleThreadExecutor();
        }
        return sSingleExecutor;
    }

    public static ExecutorService getCachedExecutor() {
        if (sCachedExecutor == null || sCachedExecutor.isShutdown()) {
            sCachedExecutor = Executors.newCachedThreadPool(DefaultThread::new);
        }
        return sCachedExecutor;
    }

    public static ExecutorService getFixedExecutor() {
        if (sFixedExecutor == null || sFixedExecutor.isShutdown()) {
            sFixedExecutor = Executors.newFixedThreadPool(4, DefaultThread::new);
        }
        return sFixedExecutor;
    }
}
