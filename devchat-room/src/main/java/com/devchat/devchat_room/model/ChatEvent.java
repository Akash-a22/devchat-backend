package com.devchat.devchat_room.model;

import lombok.Data;

@Data
public class ChatEvent {
    private String id;
    private String roomId;
    private EventType type;
}
