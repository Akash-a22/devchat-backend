package com.devchat.devchat_room.util;

import com.devchat.devchat_room.model.Increment;
import com.devchat.devchat_room.model.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Component
@Slf4j
public class CommonUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    private MongoTemplate mongoTemplate;


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
    public ExecutorService getExecutorService(){
        ExecutorService executorService= Executors.newFixedThreadPool(5);
        return executorService;
    }

    public void shutDownExecutorService(List<Future<?>> futures, ExecutorService executorService) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception while waiting for completion of thread {} \n {}", e.getMessage() , Arrays.toString(e.getStackTrace()));
            }
        }
        executorService.shutdown();
    }
    public String generateKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public String getId(String type){
        if(StringUtils.equalsIgnoreCase(type , "USER")){
            return "USER" + getNumber(type);
        } else if (StringUtils.equalsIgnoreCase(type , "ROOM")) {
            return "ROOM" + getNumber(type);
        }
        return Strings.EMPTY;
    }
    private long getNumber(String type){
        Query query = Query.query(Criteria.where("type").is(type));
        Increment increment = mongoTemplate.findOne(query, Increment.class, DBCollection.increment.name());
        CompletableFuture .runAsync( () -> mongoTemplate.updateFirst(query, new Update().set("value" , increment.getValue() + 1) , DBCollection.increment.name()));
        return increment.getValue();
    }
}
