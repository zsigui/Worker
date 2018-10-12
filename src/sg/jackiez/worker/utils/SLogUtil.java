package sg.jackiez.worker.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import sg.jackiez.worker.utils.common.CommonUtil;
import sg.jackiez.worker.utils.thread.DefaultThread;

public final class SLogUtil {

    public enum Level {

        VERBOSE("VERBOSE"), DEBUG("DEBUG"), INFO("INFO"), WARN("WARN"), ERROR("ERROR"), ASSERT("ASSERT");

        String mTag;
        Level(String tag) {
            mTag = tag;
        }

        @Override
        public String toString() {
            return mTag;
        }
    }

    private static final String DEFAULT_TAG = "SLogUtil";

    private static final String PATH_PROFIT_LOG = "log";
    private static Level sDebugLevel = Level.VERBOSE;

    private static boolean sIsPrintFile;

    public static void setPrintFile(boolean printFile) {
        sIsPrintFile = printFile;
    }

    public static void setDebugLevel(Level debugLevel) {
        sDebugLevel = debugLevel;
    }

    public static void v(Object msg) {
        log(Level.VERBOSE, DEFAULT_TAG, msg == null ? "null" : msg.toString());
    }

    public static void d(Object msg) {
        log(Level.DEBUG, DEFAULT_TAG, msg == null ? "null" : msg.toString());
    }

    public static void i(Object msg) {
        log(Level.INFO, DEFAULT_TAG, msg == null ? "null" : msg.toString());
    }

    public static void w(Object msg) {
        log(Level.WARN, DEFAULT_TAG, msg == null ? "null" : msg.toString());
    }

    public static void e(Object msg) {
        log(Level.ERROR, DEFAULT_TAG, msg == null ? "null" : msg.toString());
    }

    public static void v(Throwable t) {
        log(Level.VERBOSE, DEFAULT_TAG, t);
    }

    public static void d(Throwable t) {
        log(Level.DEBUG, DEFAULT_TAG, t);
    }

    public static void i(Throwable t) {
        log(Level.INFO, DEFAULT_TAG, t);
    }

    public static void w(Throwable t) {
        log(Level.WARN, DEFAULT_TAG, t);
    }

    public static void e(Throwable t) {
        log(Level.ERROR, DEFAULT_TAG, t);
    }

    public static void v(String tag, Object msg) {
        log(Level.VERBOSE, tag, msg == null ? "null" : msg.toString());
    }

    public static void d(String tag, Object msg) {
        log(Level.DEBUG, tag, msg == null ? "null" : msg.toString());
    }

    public static void i(String tag, Object msg) {
        log(Level.INFO, tag, msg == null ? "null" : msg.toString());
    }

    public static void w(String tag, Object msg) {
        log(Level.WARN, tag, msg == null ? "null" : msg.toString());
    }

    public static void e(String tag, Object msg) {
        log(Level.ERROR, tag, msg == null ? "null" : msg.toString());
    }

    public static void v(String tag, Throwable t) {
        log(Level.VERBOSE, tag, t);
    }

    public static void d(String tag, Throwable t) {
        log(Level.DEBUG, tag, t);
    }

    public static void i(String tag, Throwable t) {
        log(Level.INFO, tag, t);
    }

    public static void w(String tag, Throwable t) {
        log(Level.WARN, tag, t);
    }

    public static void e(String tag, Throwable t) {
        log(Level.ERROR, tag, t);
    }

    /* =========================== Real Call Start ==================================== */

    private static String getStackTrace(Throwable tr) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        String message = sw.toString();

        String traceString[] = message.split("\\n\\t");
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        int count = (traceString.length > 6 ? 6 : traceString.length);
        for (int i = 0; i < count; i++) {
            sb.append(traceString[i]).append("\n");
        }
        return sb.toString();
    }

    private static void log(Level level, String tag, Throwable t) {
        if (sDebugLevel.ordinal() > level.ordinal()) {
            // 当前打印等级超过
            return;
        }
        log(level, tag, getStackTrace(t));
    }

    private static void log(Level level, String tag, String msg) {
        if (sDebugLevel.ordinal() > level.ordinal()) {
            // 当前打印等级超过
            return;
        }
        String log = constructStackString(level, tag, msg);
        System.out.print(log);

        if (sIsPrintFile && level.ordinal() > Level.DEBUG.ordinal()) {
            // 只打印Info级别以上的
            logFile(log);
        }
    }

    private static String constructStackString(Level level, String tag, String msg) {
        String result;
        StackTraceElement[] ts = Thread.currentThread().getStackTrace();
        if (ts.length > 3) {
            StackTraceElement caller = ts[3];
            String file = (caller.getFileName() != null && caller.getLineNumber() >= 0) ?
                    ("(" + caller.getFileName() + ":" + caller.getLineNumber() + ")") :
                    (caller.getFileName() != null ?  "("+caller.getFileName()+")" : "(Unknown Source)");
            String method = caller.isNativeMethod() ? caller.getMethodName() + "(Native Method)" : caller.getMethodName();
            result = String.format("%s [%s]/[%s#%s] [%s]：%s\n", DateUtil.formatCurrentTime(), level.toString(),
                    file, method, tag, msg);
        } else {
            result = String.format("%s [%s] [%s]：%s\n", DateUtil.formatCurrentTime(), level.toString(), tag, msg);
        }
        return result;
    }
    /* =========================== Real Call End ==================================== */

    /* ========================== 打印文件线程Start ================================= */

    private static ArrayList<String> sMsgList = new ArrayList<>();
    private static Thread sFileLogThread;
    private static boolean mIsRunning;

    private static void logFile(String line) {
        if (CommonUtil.isEmpty(line)) {
            return;
        }
        if (line.length() > 200) {
            // 限制两百个
            return;
        }
        synchronized (sMsgList) {
            sMsgList.add(line);
        }
        startThread();
    }

    private static void fileLog(String line) {
        long curTime = System.currentTimeMillis();
        String date = DateUtil.formatDate(curTime);
        File f = FileUtil.getFileBaseCurrentWork(PATH_PROFIT_LOG + File.separator + date + ".log");
        if (f == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f, true);
            fos.write(line.getBytes(Config.DEFAULT_SYS_CHARSET));
            fos.write('\n');
            fos.flush();
        } catch (IOException ignored) {
        } finally {
            FileUtil.closeIO(fos);
        }
    }

    private static void startThread() {
        if (sFileLogThread != null) {
            synchronized (sFileLogThread) {
                sFileLogThread.notify();
            }
            return;
        }
        mIsRunning = true;
        sFileLogThread = new DefaultThread(() -> {
            while (mIsRunning) {
                StringBuilder builder = new StringBuilder();
                synchronized (sMsgList) {
                    for (String s : sMsgList) {
                        builder.append(s);
                    }
                    sMsgList.clear();
                }
                fileLog(builder.toString());

                synchronized (sFileLogThread) {
                    try {
                        sFileLogThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sFileLogThread.setDaemon(true);
        sFileLogThread.setPriority(Thread.NORM_PRIORITY - 2);
        sFileLogThread.start();
    }

    /* =========================== 打印文件线程End ================================== */

}
