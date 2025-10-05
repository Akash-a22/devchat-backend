package com.devchat.devchat_room.service.impl;

import com.devchat.devchat_room.dao.ChatDao;
import com.devchat.devchat_room.model.ChatMessage;
import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.service.ChatService;
import com.devchat.devchat_room.util.CommonUtil;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ChatDao chatDao;

    @Override
    public ResponseDTO getMessageOfRoom(String roomId) {
        if (StringUtils.isBlank(roomId)) {
            return commonUtil.prepareResponse(new ResponseDTO(), null, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "RoomId is empty");
        }
        List<ChatMessage> messages= chatDao.getMessageByRoomId(roomId);
        if(CollectionUtils.isEmpty(messages)){
            return commonUtil.prepareResponse(new ResponseDTO(), null, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Chat is empty");
        }
        List<ChatMessage> sorted = messages.stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedOn).reversed())
                .collect(Collectors.toList());

        List<ChatMessage> latest100 = sorted.stream()
                .limit(100)
                .sorted(Comparator.comparing(ChatMessage::getCreatedOn))
                .collect(Collectors.toList());

        List<String> toDelete = sorted.stream()
                .skip(100)
                .map(ChatMessage::getId)
                .collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(toDelete)) {
            CompletableFuture.runAsync(() -> chatDao.removeChatMessages(toDelete));
        }
        return commonUtil.prepareResponse(new ResponseDTO(),latest100 , "SUCCESS",HttpStatus.OK.value(), "Message retrieved successfully");
    }

    @Override
    public ResponseDTO deleteMessage(String id) {
        if(StringUtils.isBlank(id)){
            return commonUtil.prepareResponse(new ResponseDTO(), null , "BAD_REQUEST" , HttpStatus.BAD_REQUEST.value(),  "message id is blank");
        }
        DeleteResult deleteResult = chatDao.removeChatMessageById(id);
        if(ObjectUtils.isNotEmpty(deleteResult) && deleteResult.getDeletedCount() > 0){
            return commonUtil.prepareResponse(new ResponseDTO(), null , "SUCCESS" , HttpStatus.OK.value(),  "message deleted for all users");
        }
        return commonUtil.prepareResponse(new ResponseDTO(), null , "NO_CONTENT" , HttpStatus.NO_CONTENT.value(),  "message not found with given id");
    }
}
