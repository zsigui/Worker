package sg.jackiez.worker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String formatCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.sss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }
}
