package com.netty.socket.service;

import com.netty.socket.netty.NettyClient;
import com.netty.socket.netty.ClientHandler;
import com.netty.socket.netty.listener.NettyMessageListener;
import com.netty.socket.netty.entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.reactivex.subjects.PublishSubject;
import org.json.JSONObject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

@Service
public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final NettyClient messageClient;
    private final ClientHandler clientHandler;
    private final PublishSubject<NettyMessage> messagePublisher = PublishSubject.create();
    private int TIMEOUT_LEN = 30;
    @Autowired
    public ClientService(@Value("${hesab.host}") String host, @Value("${hesab.port}") int port){
        clientHandler = new ClientHandler<>(
                new NettyMessageListener<>() {
                    @Override
                    public boolean canHandle(NettyMessage nettyMessage) {
                        System.out.println("Can handle");
                        return true;
                    }

                    @Override
                    public boolean onReceive(ChannelHandlerContext ctx, NettyMessage nettyMessage) {
                        messagePublisher.onNext(nettyMessage);
                        return true;
                    }
                }
        );
        this.messageClient = new NettyClient(host,port,clientHandler);
    }
    @PostConstruct
    public void setup() {
        messageClient.run();
    }

    public NettyMessage doBi() throws InterruptedException {
        NettyMessage message = new NettyMessage();
        JSONObject object = new JSONObject();
        object.put("msg","Hello Mr Server!!");
        message.setMessageId(2340345);
        message.setData(object.toString());
        messageClient.send(message);
        return filterMessage(message);
    }

    private NettyMessage filterMessage(NettyMessage reqMsg){
        try{
            return messagePublisher
                    .filter(response -> {
                        double reqMsgId = reqMsg.getMessageId();
                        double resMsgId = response.getMessageId();
                        return reqMsgId == resMsgId;
                    }).timeout(TIMEOUT_LEN, TimeUnit.SECONDS)
                    .blockingFirst();
        }catch (Exception e){
            NettyMessage notFoundMsg = new NettyMessage();
            notFoundMsg.setMessageId(-1);
            notFoundMsg.setData("Filed to find the request");
            return notFoundMsg;
        }
    }
}
