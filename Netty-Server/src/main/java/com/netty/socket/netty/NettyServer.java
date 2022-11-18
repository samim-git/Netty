package com.netty.socket.netty;

import com.netty.socket.netty.coder.RequestDecoder;
import com.netty.socket.netty.coder.ResponseEncoder;
import com.netty.socket.netty.entity.NettyMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    private final int port;
    private final ServerBootstrap serverBootstrap;
    private final ServerHandler serverHandler;
    private ChannelFuture channelFuture;

    public NettyServer(int port, ServerHandler serverHandler){
        this.port = port;
        this.serverHandler = serverHandler;
        EventLoopGroup trafficEventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup connectionEventLoopGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap()
            .group(connectionEventLoopGroup, trafficEventLoopGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch){
                    ch.pipeline().addLast(
                            new RequestDecoder(),
                            new ResponseEncoder(),
                            serverHandler);

                }
            });
    }
    public void run(){
        try {
            channelFuture = serverBootstrap.bind(port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
