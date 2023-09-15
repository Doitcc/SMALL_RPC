package com.caox.utils;

import com.caox.client.RpcClient;
import com.caox.message.type.RequestMessage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理工具类
 */
public class ProxyUtil implements InvocationHandler {
    
    /**
     * 创建代理对象（即被调用的方法所在的类）
     */
    public <T> T getProxy(Class<T> target) {
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, this);
    }

    //代理对象调用了其中的方法，该方法自动执行
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RequestMessage requestMessage = RequestMessage.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterValue(args)
                .parameterTypes(method.getParameterTypes())
                .returnTypes(method.getReturnType())
                .build();
        /*requestMessage.setMessageId(MessageIdUtil.increase());*/
        requestMessage.setMessageId(0);
        
        return RpcClient.sendRequest(requestMessage);
    }
}
