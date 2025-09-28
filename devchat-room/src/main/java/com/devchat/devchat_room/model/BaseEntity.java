package com.devchat.devchat_room.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class BaseEntity {

    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;


    public void updateEntity(String name){
        this.setModifiedBy(name);
        this.setModifiedOn(LocalDateTime.now());
    }

    public void createEntity(String name){
        this.setCreatedBy(name);
        this.setCreatedOn(LocalDateTime.now());
        this.setModifiedBy(name);
        this.setModifiedOn(LocalDateTime.now());
    }
}
