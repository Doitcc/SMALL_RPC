package com.caox.handler;

import com.caox.message.type.ClientHeartMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端心跳处理器，目的是每隔一段时间自动发一个消息，防止连接断开
 */
@Slf4j
@ChannelHandler.Sharable
public class ClientHeartHandler extends ChannelDuplexHandler {

    /**
     * 出栈入栈都能处理(读写事件都能处理)
     *
     * @param ctx
     * @param evt 事件类型
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            //如果当前事件的状态是WRITER_IDLE，且一段时间没有发送数据
            if (event.state() == IdleState.WRITER_IDLE) {
                log.info("发送心跳消息");
                //发送失败直接关闭连接
                ctx.writeAndFlush(new ClientHeartMessage()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
    }
}
