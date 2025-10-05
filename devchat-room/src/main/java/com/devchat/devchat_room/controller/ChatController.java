package com.devchat.devchat_room.controller;

import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/{roomId}")
    public ResponseDTO getMessageOfRoom(@PathVariable String roomId){
        return chatService.getMessageOfRoom(roomId);
    }

    @DeleteMapping("/{id}")
    public ResponseDTO deleteMessage(@PathVariable String id){
        return chatService.deleteMessage(id);
    }
}
