package com.caox.message.type;

import com.caox.message.ResponseEnum;
import com.caox.message.RpcMessage;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应消息
 */
@Data
public class ResponseMessage<T> extends RpcMessage implements Serializable {

    //状态码
    private Integer statusCode;

    //响应信息
    private String msg;

    //响应数据
    private T data;

    //成功调用
    public ResponseMessage<T> success(T Data) {
        ResponseMessage<T> message = new ResponseMessage<>();
        message.statusCode = ResponseEnum.SUCCESS.getStatusCode();
        message.msg = ResponseEnum.SUCCESS.getMsg();
        message.data = Data;
        return message;
    }

    //失败调用
    public ResponseMessage<T> fail(T exception) {
        ResponseMessage<T> message = new ResponseMessage<>();
        message.statusCode = ResponseEnum.FAIL.getStatusCode();
        message.msg = ResponseEnum.FAIL.getMsg();
        message.data = exception;
        return message;
    }


    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
