package com.devchat.devchat_room.dao.impl;

import com.devchat.devchat_room.dao.ChatDao;
import com.devchat.devchat_room.model.ChatMessage;
import com.devchat.devchat_room.util.DBCollection;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatDaoImpl implements ChatDao {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveMessage(ChatMessage message) {
        mongoTemplate.save(message, DBCollection.message.name());
    }

    @Override
    public List<ChatMessage> getMessageByRoomId(String roomId) {
        return mongoTemplate.find(Query.query(Criteria.where("roomId").is(roomId)), ChatMessage.class, DBCollection.message.name());
    }

    @Override
    public void removeChatMessages(List<String> toDelete) {
        Query query = Query.query(Criteria.where("_id").in(toDelete));
        mongoTemplate.remove(query, ChatMessage.class, DBCollection.message.name());
    }

    @Override
    public DeleteResult removeChatMessageById(String id) {
        return mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)) , ChatMessage.class , DBCollection.message.name());
    }
}
