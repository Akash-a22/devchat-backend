package com.devchat.devchat_room.dao;

import com.mongodb.client.result.DeleteResult;

public interface UserDao {
    DeleteResult deleteUserById(String id);
}
