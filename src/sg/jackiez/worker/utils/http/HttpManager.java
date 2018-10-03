package sg.jackiez.worker.utils.http;

import com.sun.deploy.net.HttpRequest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.utils.Config;
import sg.jackiez.worker.utils.IOUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.common.CommonUtil;

public class HttpManager {

    private static final String TAG = "HttpUtil";
    private static final String PROPERTY_AUTHORIZATION = "Proxy-Authorization";
    private static final String PROPERTY_USER_AGENT = "User-Agent";
    private static final String PROPERTY_ACCEPT = "Accept";
    private static final String TYPE_TEXT = "text/plain;charset=UTF-8";
    private static final String TYPE_URL_ENCODED = " application/x-www-form-urlencoded; charset=UTF-8";
    private static final String TYPE_JSON = "application/json;charset=UTF-8";
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Mobile Safari/537.36";
    private static final String DEFAULT_ACCEPT = "*/*";

    private static final int CONNECTION_TIMEOUT = 60_000;
    private static final int READ_TIMEOUT = 30_000;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private static final class SingletonHolder {
        static HttpManager sInstance = new HttpManager();
    }

    public static HttpManager get() {
        return SingletonHolder.sInstance;
    }

    /**
     * 进行Https连接方式的协议工厂
     */
    private SSLSocketFactory mSSLSocketFactory;

    /**
     * 判断并传递正文内容
     */
    private void writeOutputData(HttpURLConnection connection, byte[] outputData) throws IOException {
        switch (connection.getRequestMethod()) {
            case "PUT":
            case "POST":
                connection.setDoInput(true);
                if (!CommonUtil.isEmpty(outputData)) {
                    connection.setDoOutput(true);
                    connection.setFixedLengthStreamingMode(outputData.length);
                    BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
                    os.write(outputData);
                    os.flush();
                }
                break;
        }
    }

    /**
     * 根据Url打开Http连接
     */
    private HttpURLConnection openConnection(URL url, ProxyInfo proxy) throws IOException {

        // 处理代理信息
        Proxy prx = Proxy.NO_PROXY;
        String validStr = null;
        if (proxy != null) {
            prx = new Proxy(proxy.getProxyType(),
                    new InetSocketAddress(proxy.getAddress(), proxy.getPort()));
            if (proxy.isNeedValid()) {
                validStr = "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s",
                        proxy.getUserName(), proxy.getPwd()).getBytes(Config.DEFAULT_SYS_CHARSET));
            }
        }

        HttpURLConnection connection;
        if (proxy != null) {
            connection = (HttpURLConnection) url.openConnection(prx);
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }

        if (validStr != null) {
            // 有代理且需要验证
            connection.setRequestProperty(PROPERTY_AUTHORIZATION, validStr);
        }

        // 设置初始默认请求属性
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);

        // 判断是否Https并进行调用
        if ("https".equals(url.getProtocol().toLowerCase())) {
            if (mSSLSocketFactory == null) {
                mSSLSocketFactory = createDefaultSSLSocketFactory();
            }
            if (mSSLSocketFactory != null) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(mSSLSocketFactory);
            }
        }

        return connection;
    }

    private SSLSocketFactory createDefaultSSLSocketFactory() {
        try {
            // 设置默认自定义Https的认证方式
            TrustManager[] tm = {new TrustAllX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(new NotVerifyHostnameVerifier());
            return sslSocketFactory;
        } catch (Exception e) {
//            SLogUtil.e(TAG, e);
        }
        return null;
    }

    /**
     * 往请求连接中添加所有头属性信息
     */
    private void addHeaders(URLConnection connection, Map<String, String> headers, String contentType) {
        // 添加通用头
        connection.addRequestProperty(PROPERTY_USER_AGENT, DEFAULT_USER_AGENT);
        connection.addRequestProperty(PROPERTY_ACCEPT, DEFAULT_ACCEPT);
        connection.setRequestProperty(HttpRequest.CONTENT_TYPE, contentType);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.addRequestProperty(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * 判断是否是gzip压缩流
     */
    private boolean isGzipStream(final HttpURLConnection urlConnection) {
        String encoding = urlConnection.getContentEncoding();
        return encoding != null && encoding.contains("gzip");
    }

    private byte[] doRequest(String method, String urlStr, byte[] outputData,
                             String contentType, Map<String, String> headers, ProxyInfo proxy) {
        HttpURLConnection connection = null;
        long startTime = System.currentTimeMillis();
        try {
            SLogUtil.d(TAG, String.format("doRequest: method = %s, url = %s", method, urlStr));
            URL url = new URL(urlStr);
            connection = openConnection(url, proxy);
            connection.setRequestMethod(method);
            addHeaders(connection, headers, contentType);
            writeOutputData(connection, outputData);

            InputStream in = null;
            try {
                int code = connection.getResponseCode();
                if (code >= 200 && code < 300) {
                    SLogUtil.d(TAG, "doRequest success!");
                    in = connection.getInputStream();
                    if (isGzipStream(connection)) {
                        in = new GZIPInputStream(in);
                    }
                    return IOUtil.readBytes(in);
                }

                // 失败，打印下错误日志
                printConnRespCode(connection);
                return IOUtil.readBytes(connection.getErrorStream());
            } catch (Exception e) {
                SLogUtil.e(TAG, e);
                IOUtil.closeIO(in);
            }

            // 失败，打印下错误日志
            printConnRespCode(connection);


        } catch (Exception e) {
            SLogUtil.e(TAG, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            SLogUtil.v(TAG, String.format("doRequest : url = %s, method = %s, spend time = %d ms", urlStr,
                    method, (System.currentTimeMillis() - startTime)));
        }

        return null;
    }

    private void printConnRespCode(HttpURLConnection connection) throws IOException {
        SLogUtil.d(TAG, String.format("code: %d, message: %s", connection.getResponseCode(),
                connection.getResponseMessage()));
    }


    public String doGet(String url) {
        return doGet(url, null);
    }

    public String doGet(String url, Map<String, String> params) {
        if (CommonUtil.isEmpty(url)) {
            SLogUtil.d(TAG, "doGet request url is " + url);
            return "";
        }

        String paramStr = HttpUtil.convertMapToEncodedUrlParam(params);
        if (!CommonUtil.isEmpty(paramStr)) {
            url = HttpUtil.spliceUrlAndParam(url, paramStr);
        }
        byte[] result = doRequest(METHOD_GET, url, null,
                TYPE_TEXT, null, OkConfig.IS_USE_PROXY ? OkConfig.PROXY_INFO : null);
        return CommonUtil.isEmpty(result) ? "" : CommonUtil.bytesToStr(result);
    }

    public String doPost(String url, Map<String, String> params) {
        if (CommonUtil.isEmpty(url)) {
            SLogUtil.d(TAG, "doGet request url is " + url);
            return "";
        }

        String paramStr = HttpUtil.convertMapToEncodedUrlParam(params);
        SLogUtil.d(TAG, paramStr);
        byte[] result = doRequest(METHOD_POST, url,
                CommonUtil.isEmpty(paramStr) ? null : CommonUtil.strToByte(paramStr),
                TYPE_URL_ENCODED, null, OkConfig.IS_USE_PROXY ? OkConfig.PROXY_INFO : null);
        return CommonUtil.isEmpty(result) ? "" : CommonUtil.bytesToStr(result);
    }
}
