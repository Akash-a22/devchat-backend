package com.devchat.devchat_room.service;

import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.model.User;

public interface UserService {


    ResponseDTO createUser(User user);

    ResponseDTO deleteUserById(String id);

    ResponseDTO getUserById(String id);

    ResponseDTO getAllUser();
}
