package com.netty.socket.controller;

import com.netty.socket.service.YourService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/switch")
public class SwitchController {
    final YourService yourService;

    public SwitchController(YourService yourService) {
        this.yourService = yourService;
    }
}
