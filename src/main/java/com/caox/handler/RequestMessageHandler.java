package com.caox.handler;

import com.caox.message.type.RequestMessage;
import com.caox.message.type.ResponseMessage;
import com.caox.register.ServiceProvider;
import com.caox.register.impl.ServiceProviderImpl;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 请求消息处理器，该处理器为入栈处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class RequestMessageHandler extends SimpleChannelInboundHandler<RequestMessage> {

    //本地服务注册
    ServiceProvider serviceProvider = new ServiceProviderImpl();

    ResponseMessage<Object> response = new ResponseMessage<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) {
        try {
            //获取调用方法（服务）的接口全限定类名
            String interfaceName = msg.getInterfaceName();

            //通过接口全限定类名找到服务的全限定类名
            String serviceName = serviceProvider.getService(interfaceName);

            //反射调用方法（服务）得到返回值
            Class<?> clazz = Class.forName(serviceName);
            Object service = clazz.newInstance();
            Method method = clazz.getMethod(msg.getMethodName(), msg.getParameterTypes());

            //@TODO 如果是对象参数，那么此方法行不通，需要将消息的args再次封装

            /* System.out.println(msg.getParameterValue()); 因为该信息是数组，所以直接打印是打印其地址，需要转换*/
            Object result = method.invoke(service, Arrays.toString(msg.getParameterValue()));

            //@TODO 这个response里面没有messageId，所以需要设置，但是msg里面没有真实的messagId（）

            // 调用成功
            ctx.writeAndFlush(response.success(result));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | InstantiationException | ClassNotFoundException e) {
            log.error("远程调用出错：" + e);
            // 异常调用
            ctx.writeAndFlush(response.fail("远程调用出错"));
        }
    }
}
