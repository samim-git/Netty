package com.netty.socket.netty;

import com.netty.socket.netty.listener.NettyMessageListener;
import com.netty.socket.netty.entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.slf4j.LoggerFactory.getLogger;

public class ClientHandler<T extends NettyMessage> extends ChannelInboundHandlerAdapter {
    private final Logger logger = getLogger(ClientHandler.class);
    private final List<NettyMessageListener<T>> messageListeners = new CopyOnWriteArrayList<>();
    private final boolean failOnError;

    public ClientHandler(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public ClientHandler() {
        this(true);
    }

    @SafeVarargs
    public ClientHandler(NettyMessageListener<T>... listeners) {
        this(true);
        messageListeners.addAll(Arrays.asList(listeners));
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Channel Active");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof NettyMessage)
            doHandleMessage(ctx,msg);
    }

    private void doHandleMessage(ChannelHandlerContext ctx, Object msg) {
        T nettyMessage;
        try {
            //noinspection unchecked
            nettyMessage = (T) msg;
        } catch (ClassCastException e) {
            logger.debug("IsoMessage subclass {} is not supported by {}. Doing nothing.", msg.getClass(), getClass());
            return;
        }

        boolean applyNextListener = true;
        final int size = messageListeners.size();
        for (int i = 0; applyNextListener && i < size; i++) {
            NettyMessageListener<T> messageListener = messageListeners.get(i);
            try {
                if (messageListener.canHandle(nettyMessage)) {
                    applyNextListener = messageListener.onReceive(ctx, nettyMessage);
                    if (!applyNextListener) {
                        logger.trace("Stopping further procession of message {} after handler {}", nettyMessage,
                                messageListener);
                    }
                }
            } catch (Exception e) {
                logger.debug("Can't evaluate {}.apply({})", messageListener, nettyMessage.getClass(), e);
                if (failOnError) {
                    throw e;
                }
            }
        }
    }

    public void addListener(NettyMessageListener<T> listener) {
        Objects.requireNonNull(listener, "IsoMessageListener is required");
        messageListeners.add(listener);
    }

    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    public final void addListeners(NettyMessageListener<T>... listeners) {
        Objects.requireNonNull(listeners, "IsoMessageListeners must not be null");
        for (NettyMessageListener<T> listener : listeners) {
            addListener(listener);
        }
    }

    public void removeListener(NettyMessageListener<T> listener) {
        messageListeners.remove(listener);
    }
//    public void sendData(){
//        RequestData msg = new RequestData();
//        msg.setIntValue(123);
//        msg.setStringValue(
//                "all work and no play makes jack a dull boy");
//        ChannelFuture future = channelHandlerContext.writeAndFlush(msg);
//    }
}
