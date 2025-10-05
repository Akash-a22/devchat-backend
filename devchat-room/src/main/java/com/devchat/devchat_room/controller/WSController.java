package com.devchat.devchat_room.controller;

import com.devchat.devchat_room.model.ChatEvent;
import com.devchat.devchat_room.model.ChatMessage;
import com.devchat.devchat_room.model.RoomEvent;
import com.devchat.devchat_room.service.WSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WSController {

    @Autowired
    private WSService wsService;

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage message, Principal principal) {
        wsService.saveAndBroadcastMessage(message , principal.getName());
    }

    @MessageMapping("/room")
    public void joinRoom(Principal principal, @Payload RoomEvent roomEvent) {
        wsService.room(roomEvent, principal.getName());
    }

    @MessageMapping("/message")
    public void message(@Payload ChatEvent chatEvent, Principal principal){
        wsService.message(chatEvent , principal.getName());
    }
}
