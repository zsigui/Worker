package sg.jackiez.worker.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class SLogUtil {

    public enum Level {

        VERBOSE("V"), DEBUG("D"), INFO("I"), WARN("W"), ERROR("E"), NONE("N");

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

    private static Level sDebugLevel = Level.VERBOSE;

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
        StackTraceElement[] ts = Thread.currentThread().getStackTrace();
        if (ts.length > 3) {
            StackTraceElement caller = ts[3];
            String file = (caller.getFileName() != null && caller.getLineNumber() >= 0) ?
                    ("(" + caller.getFileName() + ":" + caller.getLineNumber() + ")") :
                    (caller.getFileName() != null ?  "("+caller.getFileName()+")" : "(Unknown Source)");
            String method = caller.isNativeMethod() ? caller.getMethodName() + "(Native Method)" : caller.getMethodName();
            System.out.printf("%s %s/[%s#%s] [%s]：%s\n", DateUtil.formatCurrentTime(), level.toString(),
                    file, method, tag, msg);
        } else {
            System.out.printf("%s [%s] [%s]：%s\n", DateUtil.formatCurrentTime(), level.toString(), tag, msg);
        }
    }

    /* =========================== Real Call End ==================================== */
}
