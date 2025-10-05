package com.devchat.devchat_room.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
public class RoomEvent {
    private String roomId;
    private String userId;
    private String username;
    private EventType type;
}
