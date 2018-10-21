package sg.jackiez.worker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static TimeZone UTCTimeZone = TimeZone.getTimeZone("UTC");
    private static TimeZone GMT8TimeZone = TimeZone.getTimeZone("GMT+8");
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
        sdf.setTimeZone(GMT8TimeZone);
        return sdf.format(new Date(time));
    }

    public static String formatISOTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        sdf.setTimeZone(UTCTimeZone);
        return sdf.format(new Date(time));
    }

    public static long getTimeFromISOText(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        sdf.setTimeZone(UTCTimeZone);
        try {
            return sdf.parse(time).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }
}
