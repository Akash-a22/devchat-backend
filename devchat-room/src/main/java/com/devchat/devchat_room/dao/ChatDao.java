package com.devchat.devchat_room.dao;

import com.devchat.devchat_room.model.ChatMessage;
import com.mongodb.client.result.DeleteResult;

import java.util.List;

public interface ChatDao {
    void saveMessage(ChatMessage message);

    List<ChatMessage> getMessageByRoomId(String roomId);

    void removeChatMessages(List<String> toDelete);

    DeleteResult removeChatMessageById(String id);
}
