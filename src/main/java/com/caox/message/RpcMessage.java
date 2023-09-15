package com.caox.message;

import lombok.Data;

@Data
public abstract class RpcMessage {

    //消息id
    private int messageId;

    //消息类型 0为请求消息 1为响应消息 2为心跳消息
    private int messageType;


    public static final int RPC_MESSAGE_TYPE_REQUEST = 0; //请求消息
    public static final int RPC_MESSAGE_TYPE_RESPONSE = 1; //响应消息
    public static final int RPC_MESSAGE_TYPE_HEAT = 2; //心跳消息

    public abstract int getMessageType();
}
