package com.caox.handler;

import com.caox.message.type.ResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理服务器器端向客服端返回的响应消息
 * 在客户端消息发送后，客户端的defaultPromise会一直阻塞，直到promise在此设置完成
 */
@Slf4j
@ChannelHandler.Sharable
public class ResponseMessageHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    //消息号+promise线程对象
    public static final Map<Integer, Promise<Object>> PROMISE=new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
//        log.debug("{}",msg);

        //在此步骤之前PROMISE已经在RpcClient存入值了，为了防止PROMISE越来越多，
        // 所以我们需要移除remove，此方法会返回value
        Promise<Object> promise = PROMISE.remove(0);
        if(promise!=null){
            if(msg.getStatusCode()==200){
                promise.setSuccess(msg.getData());
            }else{
                promise.setFailure(new Exception(msg.getMsg()));
            }
        }else{
            log.info("线程promise不存在");
        }
    } 
}
