package sg.jackiez.worker.module.ok.network.websocket;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.internal.ConcurrentSet;
import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.common.CommonUtil;

public class WebSocketClient {

    private static final String TAG = "WebSocketClient";

    private String mUrl;
    private IWebSocketCallback mWebsocketCallback;

    private EventLoopGroup mEventGroup;
    private Channel mOriginChannel;
    private ChannelFuture mFutureChannel;

    private byte mSiteFlag = OKTypeConfig.SITE_FLAG_CNY;
    private boolean mIsConnected = false;

    private ConcurrentSet<String> mRegisterdChannel = new ConcurrentSet<>();

    public WebSocketClient(String url, IWebSocketCallback callback, byte siteFlag) {
        mUrl = url;
        mSiteFlag = siteFlag;
    }

    public void setConnected(boolean connected) {
        mIsConnected = connected;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    /**
     * 这是一个同步连接请求
     */
    public boolean open() {

        mEventGroup = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        try {
            URI uri = new URI(mUrl);
            final WebSocketClientHandler handler = new WebSocketClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(uri,
                            WebSocketVersion.V13, null, false,
                            new DefaultHttpHeaders(), Integer.MAX_VALUE),
                    mWebsocketCallback
            );
            final SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            bootstrap.group(mEventGroup)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(sslContext.newHandler(ch.alloc(), uri.getHost(),
                                    uri.getPort()));
                            p.addLast(new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    handler);
                        }
                    });


            mFutureChannel = bootstrap.connect(uri.getHost(), uri.getPort());
            mOriginChannel = mFutureChannel.sync().channel();
            // 同步等待握手完成
            boolean isSuccess = handler.handshakeFuture().sync().isSuccess();
            setConnected(isSuccess);
            return isSuccess;
        } catch (Exception e) {
            SLogUtil.e(TAG, e);
            if (mEventGroup != null) {
                mEventGroup.shutdownGracefully();
            }
            setConnected(false);
        }
        return false;
    }

    public void close() {
        if (!isConnected()) {
            return;
        }
        if (mOriginChannel != null) {
            mOriginChannel.closeFuture().syncUninterruptibly();
        }
        if (mEventGroup != null) {
            mEventGroup.shutdownGracefully();
        }
        mOriginChannel = null;
        mEventGroup = null;
        setConnected(false);
    }

    public void reOpen() {
        // 关闭之前的
        close();
        if (open()) {
            sendPing();
            // 重新添加已经注册的事件
            for (String ch : mRegisterdChannel) {
                subscribeChannel(ch);
            }
        }
    }

    private void sendMessage(String msg) {
        if (!isConnected()) {
            SLogUtil.v(TAG, "Need to connect first.");
            return;
        }
        if (CommonUtil.isEmpty(msg)) {
            SLogUtil.v(TAG, "Send message is not allowed to be null or empty.");
            return;
        }
        if (mOriginChannel == null) {
            SLogUtil.v(TAG, "Connect remote error.");
            setConnected(false);
            return;
        }
        mOriginChannel.writeAndFlush(new TextWebSocketFrame(msg));

        mOriginChannel.writeAndFlush(new BinaryWebSocketFrame());
    }

    private void sendPing(){

    }

    public void subscribeChannel(String symobl) {

    }

    public void unSubscribeChannel(String symobl) {}

}
