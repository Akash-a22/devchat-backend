package com.devchat.devchat_room.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "room")
public class Room extends BaseEntity{
    @Id
    private String id;
    private String roomId;
    private String key;
    private String name;
    private long size;
    private List<User> users;
}
