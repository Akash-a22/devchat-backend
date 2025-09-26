package com.devchat.devchat_room.model;

import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    private String createdBy;
    private String modifiedBy;
    private Date createdOn;
    private Date modifiedOn;


    public void updateEntity(String name){
//        this.setModifiedBy(name);
//        this.setModifiedOn(new Date());
    }

    public void createEntity(String name){
//        this.setCreatedBy(name);
//        this.setCreatedOn(new Date());
//        this.setModifiedBy(name);
//        this.setModifiedOn(new Date());
    }
}
