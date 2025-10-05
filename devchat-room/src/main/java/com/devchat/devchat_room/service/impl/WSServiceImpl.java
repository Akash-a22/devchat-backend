package com.devchat.devchat_room.service.impl;

import com.devchat.devchat_room.dao.ChatDao;
import com.devchat.devchat_room.model.ChatEvent;
import com.devchat.devchat_room.model.ChatMessage;
import com.devchat.devchat_room.model.EventType;
import com.devchat.devchat_room.model.RoomEvent;
import com.devchat.devchat_room.service.WSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class WSServiceImpl implements WSService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatDao chatDao;

    @Override
    public void saveAndBroadcastMessage(ChatMessage message , String name) {
        message.setId(UUID.randomUUID().toString());
        message.createEntity(name);
        chatDao.saveMessage(message);
        log.info("Generating message to roomId {} by user {}", message.getRoomId() , name);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    @Override
    public void room(RoomEvent roomEvent, String name) {
        roomEvent.setType(EventType.JOIN);
        log.info("Generating message to roomId {} by user {} for room", roomEvent.getRoomId() , name);
        messagingTemplate.convertAndSend("/topic/room/" + roomEvent.getRoomId(), roomEvent);
    }

    @Override
    public void message(ChatEvent chatEvent, String name) {
        log.info("Generating message to roomId {} by user {} for message", chatEvent.getRoomId() , name);
        messagingTemplate.convertAndSend("/topic/room/" + chatEvent.getRoomId(), chatEvent.getId());
    }
}
