package com.caox.client;

import com.caox.handler.ResponseMessageHandler;
import com.caox.message.type.RequestMessage;
import com.caox.register.NacosRegisterAndDiscovery;
import com.caox.register.impl.NacosRegisterAndDiscoveryImpl;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


@Slf4j
public class RpcClient {
    
    /**
     * 向服务端发起请求
     * @param message 请求调用方法的各项参数
     * @return
     */
    public static Object sendRequest(RequestMessage message) {
        //服务发现，根据请求调用方法的接口找到对应服务所在的地址
        NacosRegisterAndDiscovery nacosDiscovery=new NacosRegisterAndDiscoveryImpl();
        InetSocketAddress inetSocketAddress = nacosDiscovery.lookupService(message.getInterfaceName());
        
        //获取通道（同一个客户端的不同请求会重复使用一个channel）
        Channel channel = ChannelProvider.getChannel(inetSocketAddress);
        
        //创建promise来接受服务端返回的消息                              指定promise对象接受结果的线程
/*        DefaultPromise<Object> defaultPromise = new DefaultPromise<>(ChannelProvider.getChannel(inetSocketAddress).eventLoop());*/
        DefaultPromise<Object> defaultPromise = new DefaultPromise<>(ChannelProvider.group.next());
        
        //如果channel不存在或者没有连接
        if (channel == null || !channel.isActive()) {
            log.error("channel创建失败");
            ChannelProvider.group.shutdownGracefully();
            return null;
        }
        
        //@TODO message在这里还能打印真实的messageid,但传过去经过RequestMessageHandler就一直是0了
        //发送消息及成功地回调
        channel.writeAndFlush(message).addListener(future -> {
            if (future.isSuccess()) {
                log.info("客户端请求消息发送成功！");
                //@TODO 这里设置了键位0，应设置为messageId
                //这里的message.getMessageId是真实的MessageId，不会一直是0
                ResponseMessageHandler.PROMISE.put(0, defaultPromise);
            } else {
                log.error("客户端请求消息发送失败："+future.cause());
            }
        });
        
        //接收消息
        try {
            //阻塞直至消息返回
            defaultPromise.await();
            //接收成功返回结果
            if(defaultPromise.isSuccess()){
                return defaultPromise.getNow();
            }else{
                log.error("远程调用失败："+defaultPromise.cause());
                return "远程调用失败，失败原因为:"+"["+defaultPromise.cause()+"]";
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
    }
}
