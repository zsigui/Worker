package sg.jackiez.worker.utils.http;

import java.net.Proxy;

public class ProxyInfo {

    private String mAddress;
    private int mPort;
    private String mUserName;
    private String mPwd;
    private Proxy.Type mProxyType;

    private boolean isNeedValid = false;

    public ProxyInfo(Proxy.Type proxyType, String address, int port) {
        mProxyType = proxyType;
        mAddress = address;
        mPort = port;
        isNeedValid = false;
    }

    public ProxyInfo(Proxy.Type proxyType, String address, int port,
                     String userName, String pwd) {
        mAddress = address;
        mPort = port;
        mUserName = userName;
        mPwd = pwd;
        mProxyType = proxyType;
        isNeedValid = true;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getPort() {
        return mPort;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getPwd() {
        return mPwd;
    }

    public Proxy.Type getProxyType() {
        return mProxyType;
    }

    public boolean isNeedValid() {
        return isNeedValid;
    }
}
