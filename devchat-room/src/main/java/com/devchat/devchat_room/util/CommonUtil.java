package com.devchat.devchat_room.util;

import com.devchat.devchat_room.model.ResponseDTO;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class CommonUtil {

    public ResponseDTO prepareResponse(ResponseDTO responseDTO, Object data, String status, int statusCode, String message) {
        responseDTO.setResponseObject(data);
        responseDTO.setCode(statusCode);
        responseDTO.setStatus(status);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    public Update buildUpdateFromNonNullFields(Object obj) {
        Update update = new Update();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null && !"id".equalsIgnoreCase(field.getName())) {
                    update.set(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }
        return update;
    }
}
