package com.caox.message.type;

import com.caox.message.RpcMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 请求消息
 */
@Data
@AllArgsConstructor
@Builder
public class RequestMessage extends RpcMessage implements Serializable {

    //调用接口的全限定类名，服务器根据他找到对应的实现
    private String interfaceName;

    //调用方法的返回值参数类型
    private Class<?>  returnTypes;
    
    //调用的方法名
    private String methodName;

    //调用方法的参数类型
    private Class<?> [] parameterTypes;

    //调用方法的参数值
    private Object[] parameterValue;
    
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
