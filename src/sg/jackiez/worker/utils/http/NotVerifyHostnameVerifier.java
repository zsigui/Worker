package sg.jackiez.worker.utils.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import sg.jackiez.worker.utils.SLogUtil;

/**
 * 默认不验证主机Host
 */
public class NotVerifyHostnameVerifier implements HostnameVerifier {

    private static final String TAG = "NotVerifyHostnameVerifier";

    public boolean verify(String hostname, SSLSession session) {
        SLogUtil.v(TAG, "verify : hostname = " + hostname);
        return true;
    }

}
