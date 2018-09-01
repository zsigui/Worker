package sg.jackiez.worker.utils.chiper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import sg.jackiez.worker.utils.Config;
import sg.jackiez.worker.utils.SLogUtil;

/**
 * MD5摘要算法类
 * <p/>
 * Created by zsigui on 15-8-17.
 */
public final class MD5 {

    private static final String TAG = "MD5";

    /**
     * 生成摘要字节数组
     *
     * @param content
     * @return
     */
    public static byte[] genDigest(String content) {
        return genDigest(content, Config.DEFAULT_SYS_CHARSET);
    }

    /**
     * 生成摘要字节数组
     *
     * @param content 原文本内容字符串
     * @param charset 获取字节数组编码
     * @return
     */
    public static byte[] genDigest(String content, String charset) {
        try {
            return genDigest(content.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            SLogUtil.e(TAG, e);
        }
        return null;
    }

    /**
     * 生成摘要字节数组
     *
     * @param content 原文本内容字节数组
     * @return
     */
    public static byte[] genDigest(byte[] content) {
        if (content != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(content);
                return md.digest();
            } catch (Exception e) {
                SLogUtil.e(TAG, e);
            }
        }
        return null;
    }
}