package com.devchat.devchat_room.dao;

import com.devchat.devchat_room.model.User;
import com.mongodb.client.result.DeleteResult;

import java.util.List;

public interface UserDao {
    DeleteResult deleteUserById(String id);

    void saveUser(User user);

    User checkIfUserExistWithKey(String name);

    User getUserById(String id);

    List<User> getAllUser();
}
