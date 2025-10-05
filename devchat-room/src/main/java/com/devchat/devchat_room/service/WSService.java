package com.devchat.devchat_room.service;

import com.devchat.devchat_room.model.ChatEvent;
import com.devchat.devchat_room.model.ChatMessage;
import com.devchat.devchat_room.model.RoomEvent;

public interface WSService {

    void saveAndBroadcastMessage(ChatMessage message, String name);

    void room(RoomEvent roomEvent, String name);

    void message(ChatEvent chatEvent, String name);
}
