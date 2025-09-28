package com.devchat.devchat_room.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;

@Data
@Document(collection = "session")
public class Session {

    @Id
    private String id;
    private String roomId;
    private LocalDateTime createOn;
    private LocalDateTime modifiedOn;
    private LocalDateTime expireOn;
    private SessionStatus status;
}
