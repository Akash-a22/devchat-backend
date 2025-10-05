package com.devchat.devchat_room.service;

import com.devchat.devchat_room.model.ResponseDTO;

public interface ChatService {
    ResponseDTO getMessageOfRoom(String roomId);

    ResponseDTO deleteMessage(String id);
}
