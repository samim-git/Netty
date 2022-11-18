package com.netty.socket.netty.coder;

import com.netty.socket.netty.entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;


public class RequestDecoder extends ReplayingDecoder<NettyMessage> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){
        try{
            NettyMessage data = new NettyMessage();
            int strLen = in.readInt();
            data.setMessageId(in.readDouble());
            data.setData(in.readCharSequence(strLen, charset).toString());
            out.add(data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}