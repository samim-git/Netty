package com.netty.socket.netty;

import com.netty.socket.netty.coder.RequestEncoder;
import com.netty.socket.netty.coder.ResponseDecoder;
import com.netty.socket.netty.entity.NettyMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    String host;
    int port;
    ClientHandler handler;
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    ChannelFuture future;
    private final Bootstrap clientBootstrap;

    public NettyClient(String host,int port, ClientHandler clientHandler){
        this.port = port;
        this.host = host;
        this.handler = clientHandler;
        clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerGroup);
        clientBootstrap.channel(NioSocketChannel.class);
        clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                        new RequestEncoder(),
                        new ResponseDecoder(),
                        handler);
            }
        });
    }
    public void run(){
        try {
            future = clientBootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public ChannelFuture send(NettyMessage nettyMessage) throws InterruptedException {
//        RequestData msg = new RequestData();
//        msg.setIntValue(123);
//        msg.setStringValue(
//                "all work and no play makes jack a dull boy");
        return future.channel().writeAndFlush(nettyMessage).sync();
    }
}
