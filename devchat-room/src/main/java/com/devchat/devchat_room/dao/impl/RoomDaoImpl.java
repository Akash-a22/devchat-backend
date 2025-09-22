package com.devchat.devchat_room.dao.impl;

import com.devchat.devchat_room.dao.RoomDao;
import com.devchat.devchat_room.model.Room;
import com.devchat.devchat_room.util.CommonUtil;
import com.devchat.devchat_room.util.DBCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Repository;

@Repository
public class RoomDaoImpl implements RoomDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CommonUtil commonUtil;
    @Override
    public Room createRoom(Room room) {
        return mongoTemplate.insert(room, DBCollection.room.name());
    }

    @Override
    public Room getRoomByKey(String key) {
        return mongoTemplate.findOne(new Query(Criteria.where("key").is(key)),Room.class, DBCollection.room.name());
    }

    @Override
    public DeleteResult deleteRoom(String key) {
        return mongoTemplate.remove(Query.query(Criteria.where("key").is(key)),Room.class,DBCollection.room.name());
    }

    @Override
    public UpdateResult updateRoom(Room roomDB) {
        Query query=Query.query(Criteria.where("roomId").is(roomDB.getRoomId()));
        UpdateDefinition update = commonUtil.buildUpdateFromNonNullFields(roomDB);
        return mongoTemplate.updateFirst(query,update,Room.class,DBCollection.room.name());
    }
}
