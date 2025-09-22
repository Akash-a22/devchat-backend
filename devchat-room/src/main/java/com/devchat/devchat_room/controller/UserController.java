package com.devchat.devchat_room.controller;

import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.model.User;
import com.devchat.devchat_room.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseDTO createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @GetMapping
    public ResponseDTO getUserById(@RequestParam String id){
        return userService.getUserById(id);
    }

    @GetMapping("/all-user")
    public ResponseDTO getAllUser(){
        return userService.getAllUser();
    }

    @DeleteMapping
    private ResponseDTO deleteUser(@RequestParam String userId){
        return userService.deleteUserById(userId);
    }
}
