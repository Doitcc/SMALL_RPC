package com.caox.client;

import com.caox.handler.ClientHeartHandler;
import com.caox.handler.ResponseMessageHandler;
import com.caox.protocol.MyProtocol;
import com.caox.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelProvider {

    //用来映射ip套接字和channel的关系，方便下次获取 channel
    private static final Map<Integer, Channel> channelMap = new ConcurrentHashMap<>();

    //客户端
    private static Bootstrap clientBootstrap;
    
    //事件循环组
    public static NioEventLoopGroup group;

    //一个客户端调用多次ChannelProvider也只加载一次
    static {
        clientBootstrap=new Bootstrap();
        group=new NioEventLoopGroup();
        initChannel();
    }

    /**
     * 获取channel，如果没有就创建
     *
     * @param inetSocketAddress 连接地址
     * @return 数据通道
     */
    public static Channel getChannel(InetSocketAddress inetSocketAddress) {
        int port = inetSocketAddress.getPort();
        if (channelMap.containsKey(port)) {
            Channel channel = channelMap.get(port);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(port);
            }
        }
        try {
            //获取连接
            Channel channel = clientBootstrap.connect(inetSocketAddress).sync().channel();
            //将channel放入到map中，方便下次调用
            channelMap.put(port, channel);
            return channel;
        } catch (InterruptedException e) {
            log.error("客户端连接失败" + e.getMessage());
            return null;
        }
    }

    /**
     * 初始化channel
     */
    public static void initChannel() {
        //日志处理器
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        //自定义协议编解码器
        MyProtocol MY_PROTOCOL = new MyProtocol();
        //心跳处理器
        ClientHeartHandler HEART_HANDLER=new ClientHeartHandler();
        //响应处理器
        ResponseMessageHandler RESPONSE_HANDLER = new ResponseMessageHandler();

        clientBootstrap.channel(NioSocketChannel.class)
                .group(group)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProcotolFrameDecoder());
                        ch.pipeline().addLast(new IdleStateHandler(0,20,0));
                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(MY_PROTOCOL);
                        ch.pipeline().addLast(HEART_HANDLER);
                        ch.pipeline().addLast(RESPONSE_HANDLER);
                    }
                });
/*        Channel channel = clientBootstrap.connect("localhost", 8080).sync().channel();*/

/*            ChannelFuture future = channel.writeAndFlush(new RequestMessage(
                    "", String.class, "", new Class[]{String.class}, new Object[]{"张三"}
            ));
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    Throwable cause = channelFuture.cause();
                    log.error("服务器异常",cause);
                }
            });*/

/*        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });*/
    }
}
