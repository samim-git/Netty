package com.netty.socket.netty.coder;

import com.netty.socket.netty.entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class ResponseEncoder
        extends MessageToByteEncoder<NettyMessage> {

    private final Charset charset = Charset.forName("UTF-8");
    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf out){
        out.writeInt(msg.getData().length());
        out.writeDouble(msg.getMessageId());
        out.writeCharSequence(msg.getData(), charset);
    }
}
