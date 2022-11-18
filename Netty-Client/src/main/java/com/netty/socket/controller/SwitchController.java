package com.netty.socket.controller;

import com.netty.socket.netty.entity.NettyMessage;
import com.netty.socket.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/switch")
public class SwitchController {
    final ClientService clientService;

    public SwitchController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/bi")
    public ResponseEntity<NettyMessage> doBi(){
        NettyMessage returnMsg = new NettyMessage();
        try{
            returnMsg = clientService.doBi();
        }catch (InterruptedException e) {
            e.printStackTrace();
            returnMsg.setData("No response");
            returnMsg.setMessageId(0);
        }
        return ResponseEntity.ok(returnMsg);
    }
}
