package com.devchat.devchat_room.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "room")
public class Room {
    @Id
    private String id;
    private String key;
    private String name;
    private String createdBy;
    private String modifiedBy;
    private Date createdOn;
    private Date modifiedOn;
    private long personCount;
    private List<User> users;
}
