package com.devchat.devchat_room.dao.impl;

import com.devchat.devchat_room.dao.SessionDao;
import com.devchat.devchat_room.model.Session;
import com.devchat.devchat_room.util.CommonConstants;
import com.devchat.devchat_room.util.DBCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class SessionDaoImpl implements SessionDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveSession(Session session) {
        mongoTemplate.save(session, DBCollection.session.name());
    }

    @Override
    public Session getSession(String roomId) {
        return mongoTemplate.findOne(Query.query(Criteria.where(CommonConstants.ROOM_ID).is(roomId)), Session.class , DBCollection.session.name());
    }

    @Override
    public void updateSession(Session session) {
        Update update=new Update();
        update.set("expireOn",session.getExpireOn());
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(session.getId())),update,DBCollection.session.name());
    }

    @Override
    public void deleteSessionByRoomId(String roomId) {
        mongoTemplate.remove(Query.query(Criteria.where(CommonConstants.ROOM_ID).is(roomId)),DBCollection.session.name());
    }
}
