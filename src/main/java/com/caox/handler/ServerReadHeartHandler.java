package com.caox.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端空闲检测处理器，几秒钟没收到客户端发来的心跳消息就断开连接
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerReadHeartHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            //如果是读空闲事件
            if (event.state() == IdleState.READER_IDLE) {
                log.info("客户端30s没有发送消息，关闭连接");
                ctx.channel().close();
            }
        }
    }
}