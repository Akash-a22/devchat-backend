package com.devchat.devchat_room.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "increment")
public class Increment {
    @Id
    private String id;
    private String type;
    private long value;
}
