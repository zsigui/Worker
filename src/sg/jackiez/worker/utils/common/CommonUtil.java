package sg.jackiez.worker.utils.common;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import sg.jackiez.worker.utils.Config;
import sg.jackiez.worker.utils.SLogUtil;

public final class CommonUtil {

    private static final String TAG = "CommonUtil";

    /**
     * 判断给定对象是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        boolean result = obj == null;
        if (!result) {
            if (obj instanceof CharSequence) {
                result = ((String) obj).isEmpty();
            } else if (obj instanceof Map) {
                result = ((Map) obj).isEmpty();
            } else if (obj instanceof List) {
                result = ((List) obj).isEmpty();
            } else if (obj instanceof Set) {
                result = ((Set) obj).isEmpty();
            }
        }
        return result;
    }

    /**
     * 将字节数组转换成十六进制字符串
     *
     * @param bs
     * @return
     */
    public static String bytesToHex(byte... bs) {
        if (isEmpty(bs)) {
            throw new IllegalArgumentException(TAG + ".bytesToHex : param bs(byte...) is null");
        }
        StringBuilder builder = new StringBuilder();
        for (byte b : bs) {
            int bt = b & 0xff;
            if (bt < 16) {
                builder.append(0);
            }
            builder.append(Integer.toHexString(bt));
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 将十六进制字符串转换成字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(String hex) {
        if (isEmpty(hex)) {
            throw new IllegalArgumentException(TAG + ".hexToBytes : param hex(String...) is null");
        }
        if (hex.length() % 2 == 1)
            hex += '0';
        byte[] result = new byte[hex.length() / 2];
        char[] cs = hex.toLowerCase(Locale.CHINA).toCharArray();
        for (int i = 0; i < result.length; i++) {
            int pos = i * 2;
            result[i] = (byte) (charToByte(cs[pos]) << 4 | charToByte(cs[pos + 1]));
        }
        return result;
    }

    /**
     * 将字节数组转为十六进制表示的MAC地址
     *
     * @param bs
     * @return
     */
    public static String byteToMacStr(byte[] bs) {
        if (isEmpty(bs)) {
            throw new IllegalArgumentException(TAG + ".byteToMacStr : param bs(byte[]) is null");
        }
        StringBuilder sb = new StringBuilder(bs.length);
        for (byte b : bs) {
            sb.append(":");
            sb.append(bytesToHex(b));
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }

    /**
     * 将整形转换为点十六进制表示法的IP地址
     *
     * @param info
     * @return
     */
    public static String intToIpStr(int info) {
        return (info & 0xFF) + "." + (info >> 8 & 0xFF) + "." + (info >> 16 & 0xFF) + "." + (info >> 24 & 0xFF);
    }

    /**
     * 将字符转换为字节数
     *
     * @param c
     * @return
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789abcdefg".indexOf(c);
    }

    /**
     * 将字节数组转换为指定格式编码的字符串
     */
    public static String bytesToStr(byte[] data, String charset) {
        String result = null;
        try {
            result = new String(data, charset);
        } catch (UnsupportedEncodingException e) {
            SLogUtil.e(TAG, e);
        }
        return result;
    }

    /**
     * 将字节数组转换为系统默认格式编码的字符串
     */
    public static String bytesToStr(byte[] data) {
        return bytesToStr(data, Config.DEFAULT_SYS_CHARSET);
    }

    public static byte[] strToByte(String s) {
        return strToByte(s, Config.DEFAULT_SYS_CHARSET);
    }

    public static byte[] strToByte(String s, String charset) {
        try {
            return s.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            SLogUtil.d(TAG, e);
        }
        return null;
    }

    public static void copy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

}
