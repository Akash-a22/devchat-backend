package com.devchat.devchat_room.model;

import lombok.Data;

@Data
public class ResponseDTO {
    private String message;
    private String status;
    private int code;
    private Object responseObject;
}
