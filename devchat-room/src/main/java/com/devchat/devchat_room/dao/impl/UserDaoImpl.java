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

import java.util.List;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public DeleteResult deleteUserById(String id) {
        return mongoTemplate.remove(Query.query(Criteria.where("userId").is(id)), User.class, DBCollection.user.name());
    }

    @Override
    public void saveUser(User user) {
        mongoTemplate.save(user);
    }

    @Override
    public User checkIfUserExistWithKey(String name) {
        return mongoTemplate.findOne(Query.query(Criteria.where("name").is(name)), User.class , DBCollection.user.name());
    }

    @Override
    public User getUserById(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(id)), User.class , DBCollection.user.name());
    }

    @Override
    public List<User> getAllUser() {
        return mongoTemplate.findAll(User.class);
    }
}
