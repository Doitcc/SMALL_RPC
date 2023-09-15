package com.caox.message.type;

import com.caox.message.RpcMessage;

import java.io.Serializable;

public class ClientHeartMessage extends RpcMessage implements Serializable {
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_HEAT;
    }
}
