package com.netty.socket.netty.listener;

import com.netty.socket.netty.entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;

public interface NettyMessageListener<T extends NettyMessage>{
    boolean canHandle(T nettyMessage);
    boolean onReceive(ChannelHandlerContext ctx, T nettyMessage);
}
