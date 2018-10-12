package sg.jackiez.worker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static String formatCurrentTime() {
        return formatTime(System.currentTimeMillis(), "MM-dd HH:mm:ss.SSS");
    }

    public static String formatUnixTime(long time) {
        return formatTime(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDate(long time) {
        return formatTime(time, "yyyy-MM-dd");
    }

    public static String formatTime(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(new Date(time));
    }
}
