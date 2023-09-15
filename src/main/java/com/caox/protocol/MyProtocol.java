package com.caox.protocol;

import com.caox.config.SerializerConfig;
import com.caox.message.RpcMessage;
import com.caox.message.type.RequestMessage;
import com.caox.message.type.ResponseMessage;
import com.caox.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 自定义协议编解码器
 * 因为ProcotolFrameDecoder已经帮我们把黏包半包问题解决了，所以继承的是MessageToMessageCodec
 */
@Slf4j
@ChannelHandler.Sharable
public class MyProtocol extends MessageToMessageCodec<ByteBuf, RpcMessage> {
    /**
     * 编码，给数据加上头部信息，同时将数据序列化，让其可以在网络中传输
     * <p>
     *
     * 0   1   2   3   4         5                6            7   8   9   10  11    12    13  14   15  16
     * +---+---+---+---+--------+----------------+-------------+---+---+---+---+-----+-----+-----+--+----+
     * |  magic code  |version | serializerType | messageType | MessageId     | Fill |    length        |
     * +-------------+--------+----------------+--------------+--------------+-------+-----------------+
     * |                                                                                               |
     * |                                         body                                                  |
     * |                                                                                               |
     * |                                        ... ...                                               |
     * +----------------------------------------------------------------------------------------------+
     * 4B  magic code（魔数）   1B version（版本）   1B serializerType（序列化类型）    1B messageType（消息类型）
     * 4B  MessageId（消息的Id） 1B fill(填充，无意义） 4B legth(消息长度)
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        
        // 4字节的魔数
        buffer.writeBytes("doit".getBytes(StandardCharsets.UTF_8));

        // 1字节的版本,
        buffer.writeByte(1);

        // 1字节的序列化类型 jdk 0 , json 1
        buffer.writeByte(SerializerConfig.getSerializerType());

        // 1字节的消息类型
        buffer.writeByte(msg.getMessageType());

        // 4个字节消息Id（这里的messageId是真实的id）
        buffer.writeInt(msg.getMessageId());

        // 无意义，对齐填充
        buffer.writeByte(0xff);
        
        //获取序列化器并序列化
        Serializer algorithm = SerializerConfig.getSerializer();
        byte[] bytes = algorithm.serialize(msg);

        // 4个字节的消息长度
        buffer.writeInt(bytes.length);

        // 写入内容
        buffer.writeBytes(bytes);
        out.add(buffer);
    }

    /**
     * 解码，按照自定义的传输协议，将数据转为java对象
     * 自定义传输协议
     * <p>
     *     
     * 0   1   2   3   4         5                6            7   8   9   10  11    12    13  14   15  16
     * +---+---+---+---+--------+----------------+-------------+---+---+---+---+-----+-----+-----+--+----+
     * |  magic code  |version | serializerType | messageType | MessageId     | Fill |    length        |
     * +-------------+--------+----------------+--------------+--------------+-------+-----------------+
     * |                                                                                               |
     * |                                         body                                                  |
     * |                                                                                               |
     * |                                        ... ...                                               |
     * +----------------------------------------------------------------------------------------------+
     * 4B  magic code（魔数）   1B version（版本）   1B serializerType（序列化类型）    1B messageType（消息类型）
     * 4B  MessageId（消息的Id） 1B fill(填充，无意义） 4B legth(消息长度)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //4字节的魔数
        int magicCode = in.readInt();

        //1字节的版本
        byte version = in.readByte();

        //1字节的序列化类型
        byte serializerType = in.readByte();

        //1字节的消息类型
        byte messageType = in.readByte();
        
        //4字节的请求id（这里的messageId是真实的id）
        int messageId = in.readInt();

        //1字节的填充字段
        byte fill = in.readByte();

        //4字节的消息长度
        int length = in.readInt();

        //从in里面读取数据
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        //获取消息的类型
        Class<?> message = messageType == 0 ? RequestMessage.class : ResponseMessage.class;
        //将消息字节数组转变为对应的消息
        Object rpcmessage = SerializerConfig.getSerializer().deserialize(bytes, message);
        out.add(rpcmessage);
    }
}
