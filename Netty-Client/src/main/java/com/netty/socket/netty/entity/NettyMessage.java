package com.netty.socket.netty.entity;

public class NettyMessage {
    double messageId;
    String data;
    public double getMessageId() {
        return messageId;
    }
    public void setMessageId(double messageId) {
        this.messageId = messageId;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}

