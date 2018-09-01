package sg.jackiez.worker.utils.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.jackiez.worker.utils.SLogUtil;

/**
 * Created by zsigui on 15-8-21.
 */
public class StringUtil {

    private static final String TAG = "StringUtil";

    private static Matcher getMatcher(CharSequence data, String pattern, int flag) {
        Pattern p = Pattern.compile(pattern, flag);
        return p.matcher(data);
    }

    public static boolean find(CharSequence data, String pattern, boolean isSensitive) {
        return getMatcher(data, pattern, isSensitive ? 0 : Pattern.CASE_INSENSITIVE).find();
    }

    public static boolean matches(CharSequence data, String pattern, boolean isSensitive) {
        return getMatcher(data, pattern, isSensitive ? 0 : Pattern.CASE_INSENSITIVE).matches();
    }

    public static String findMatch(CharSequence data, String pattern, int groupIndex, boolean isSensitive) {
        Matcher matcher = getMatcher(data, pattern, isSensitive ? 0 : Pattern.CASE_INSENSITIVE);
        String result = null;
        if (matcher.find()) {
            result = matcher.group(groupIndex);
        }
        return result;
    }

    public static String findMatch(CharSequence data, String pattern, int groupIndex, String defaultNotFound,
                                   boolean isSensitive) {
        Matcher matcher = getMatcher(data, pattern, isSensitive ? 0 : Pattern.CASE_INSENSITIVE);
        String result;
        if (matcher.find()) {
            result = matcher.group(groupIndex);
        } else {
            result = defaultNotFound;
        }
        return result;
    }

    public static List<String> findAllMatch(CharSequence data, String pattern, int groupIndex, boolean isSensitive) {
        List<String> result = new ArrayList<>();
        Matcher matcher = getMatcher(data, pattern, isSensitive ? 0 : Pattern.CASE_INSENSITIVE);
        while (matcher.find()) {
            result.add(matcher.group(groupIndex));
        }
        return result;
    }

    /**
     * 判断字符是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 将字节数组转换为非乱码字符串(会自动检测乱码并使用常用编码进行解码，故效率会比较低)
     */
    public static String bytesToNoMessyStr(byte[] data) {
        String result = null;
        String[] charsets = new String[]{"GB2312", "UTF-8", "BIG5", "GBK", "Unicode"};
        try {
            for (String charset : charsets) {
                String tmp = new String(data, charset);
                if (!StringUtil.isMessyCode(tmp)) {
                    result = tmp;
                }
            }
        } catch (UnsupportedEncodingException e) {
            SLogUtil.e(TAG, e);
        }
        return result;
    }
}