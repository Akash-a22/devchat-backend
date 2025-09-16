package com.devchat.devchat_room.dao.impl;

import com.devchat.devchat_room.dao.UserDao;
import com.devchat.devchat_room.model.User;
import com.devchat.devchat_room.util.DBCollection;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public DeleteResult deleteUserById(String id) {
        return mongoTemplate.remove(Query.query(Criteria.where(id).is(id)), User.class, DBCollection.user.name());
    }
}
