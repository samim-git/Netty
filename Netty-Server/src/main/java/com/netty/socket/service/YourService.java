package com.netty.socket.service;

import com.netty.socket.netty.NettyServer;
import com.netty.socket.netty.ServerHandler;
import com.netty.socket.netty.listener.NettyMessageListener;
import com.netty.socket.netty.entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.reactivex.subjects.PublishSubject;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class YourService {
    private static final Logger logger = LoggerFactory.getLogger(YourService.class);
    private final NettyServer messageServer;
    private final ServerHandler serverHandler;
    @Autowired
    public YourService(@Value("${hesab.host}") String host, @Value("${hesab.port}") int port){
        serverHandler = new ServerHandler<>(
                new NettyMessageListener<>() {
                    @Override
                    public boolean canHandle(NettyMessage nettyMessage) {
                        System.out.println("can handle");
                        return true;
                    }

                    @Override
                    public boolean onReceive(ChannelHandlerContext ctx, NettyMessage nettyMessage) {
                        JSONObject object = new JSONObject(nettyMessage.getData());
                        object.put("msg","Hello from Server!!");
                        nettyMessage.setData(object.toString());
                        ctx.writeAndFlush(nettyMessage);
                        return true;
                    }
                }
        );
        messageServer = new NettyServer(port,serverHandler);
    }
    @PostConstruct
    public void setup() throws Exception {
        logger.info("starting the message client");
        messageServer.run();
        logger.info("message client started up");
        try{
//            sendEcho();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public SwitchRequest doBi() throws InterruptedException {
////        ByteBuf buffer = Unpooled.buffer();
////        ByteBufOutputStream os = new ByteBufOutputStream(buffer);
////        if (isoHeaderLength == 0) {
////            buffer.writeBytes(requestMessage.writeData());
////        } else {
////            requestMessage.write(os, isoHeaderLength);
////        }
//        messageSEr.sendStr("Hello messages");
//        try{
//            return messagePublisher
//                    .filter(response -> {
////                        String f11Req = requestMessage.getField(11).getValue().toString();
////                        String f11Res = response.getField(11).getValue().toString();
////                        if(f11Res == null) {
////                            return true;
////                        }
////                        return f11Req.equals(f11Res);
//                        return true;
//                    }).timeout(30, TimeUnit.SECONDS)
//                    .blockingFirst();
//        }catch (Exception e){
//            e.printStackTrace();
////            IsoMessage isoMessage33 = new IsoMessage();
////            isoMessage33.setField(39, new IsoValue<>(IsoType.ALPHA, "000",3));
//            return new SwitchRequest();
//        }
//    }
}
