package com.devchat.devchat_room.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message")
@Data
public class ChatMessage extends BaseEntity{
    @Id
    private String id;
    private String roomId;
    private String userName;
    private String userId;
    private String value;
}
