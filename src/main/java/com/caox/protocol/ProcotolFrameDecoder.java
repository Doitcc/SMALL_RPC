package com.caox.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 定长解码器
 * 解决黏包和半包问题
 */
public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    /**
     * 
     * @param maxFrameLength:最大长度
     * @param lengthFieldOffset：长度偏移量
     * @param lengthFieldLength：长度
     * @param lengthAdjustment：长度字段后的几个字节是真数据
     * @param initialBytesToStrip：从第几个字节开始返回第一个数据
     */
    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
