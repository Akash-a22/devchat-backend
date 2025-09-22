package com.devchat.devchat_room.service.impl;

import com.devchat.devchat_room.dao.RoomDao;
import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.model.Room;
import com.devchat.devchat_room.model.User;
import com.devchat.devchat_room.service.RoomService;
import com.devchat.devchat_room.service.UserService;
import com.devchat.devchat_room.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {


    @Autowired
    private RoomDao roomDao;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public ResponseDTO createRoom(Room room) {
        HashSet<String> response = validRoom(room);
        if (!CollectionUtils.isEmpty(response)) {
            return commonUtil.prepareResponse(new ResponseDTO(), response, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Please provide the necessary fields");
        }
        String key = commonUtil.generateKey();
        room.setKey(key);
        room.setCreatedOn(new Date());
        room.setModifiedOn(new Date());
        room.setSize(room.getSize());
        User user = room.getUsers().get(0);
        user.setKey(commonUtil.generateKey());
        user.setId(commonUtil.getId("ROOM"));

        room.createEntity(room.getName());
        roomDao.createRoom(room);
        userService.createUser(user);
        return commonUtil.prepareResponse(new ResponseDTO(), room, "CREATED", HttpStatus.CREATED.value(), "Room is created successfully");
    }

    private HashSet<String> validRoom(Room room) {
        HashSet<String> response = new HashSet<>();
        if (ObjectUtils.isEmpty(room)) {
            response.add("room is empty");
            return response;
        }
        if (StringUtils.isBlank(room.getName())) {
            response.add("name is empty");
        }
        if (CollectionUtils.isEmpty(room.getUsers())) {
            response.add("user or users are empty");
        }
        if (room.getSize() == 0 || room.getSize() > 5) {
            response.add("size is empty or size is more than 5");
        }
        return response;
    }

    @Override
    public ResponseDTO deleteRoom(String key) {
        if (StringUtils.isBlank(key)) {
            return commonUtil.prepareResponse(new ResponseDTO(), null, "NOT_FOUND", HttpStatus.NO_CONTENT.value(), "Please send correct key");
        }
        if (checkIfRoomExist(key)) {
            deleteUserByRoomKey(key);
            roomDao.deleteRoom(key);
            return commonUtil.prepareResponse(new ResponseDTO(), null, "DELETED", HttpStatus.OK.value(), "Room is deleted successfully");
        }
        return commonUtil.prepareResponse(new ResponseDTO(), null, "NOT_FOUND", HttpStatus.NO_CONTENT.value(), "Room with key doesn't exits");
    }

    @Override
    public ResponseDTO updateRoom(Room room) {
        //needtowork
        return null;
    }

    @Override
    public ResponseDTO joinRoom(Room room) {
        HashSet<String> response = validJoinRoom(room);
        if (!CollectionUtils.isEmpty(response)) {
            return commonUtil.prepareResponse(new ResponseDTO(), response, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Please provide necessary fields");
        }
        String roomKey = room.getKey();
        Room roomDB = roomDao.getRoomByKey(roomKey);
        User user = room.getUsers().get(0);
        if(isUserExitInRoom(user.getName(),roomKey)){
            return commonUtil.prepareResponse(new ResponseDTO() , null , "BAD_REQUEST" , HttpStatus.BAD_REQUEST.value(), "User with the name is already present in room , provide any other name");
        }
        prepareUserEntity(user);
        roomDB.updateEntity(user.getName());
        roomDB.getUsers().add(user);
        roomDB.setSize(roomDB.getSize() + 1);
        userService.createUser(user);
        roomDao.updateRoom(roomDB);

        return commonUtil.prepareResponse(new ResponseDTO(), null, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Room reached its max limit of participants");
    }

    private void prepareUserEntity(User user) {
        user.setUserId(commonUtil.getId("USER"));
        user.setKey(commonUtil.generateKey());
    }

    private boolean isUserExitInRoom(String userName, String roomKey) {
        //same name user can't join room
        Room room = getRoomByKey(roomKey);
        if(ObjectUtils.isNotEmpty(room) && !CollectionUtils.isEmpty(room.getUsers())){
            return room.getUsers().stream().anyMatch(u -> StringUtils.equalsIgnoreCase(u.getName() , userName));
        }
        return false;
    }

    private HashSet<String> validJoinRoom(Room room) {
        HashSet<String> response = new HashSet<>();
        if (StringUtils.isBlank(room.getRoomId())) {
            response.add("roomId is empty");
        }
        if (CollectionUtils.isEmpty(room.getUsers())) {
            response.add("user or users are empty");
        }
        Room dbRoom = getRoomByKey(room.getKey());
        if (ObjectUtils.isEmpty(dbRoom)) {
            response.add("room is not found with current key or its already closed");
            return response;
        }
        if (dbRoom.getSize() == 5) {
            response.add("room is full");
        }
        return response;
    }

    private Room getRoomByKey(String key) {
        return roomDao.getRoomByKey(key);
    }

    private void deleteUserByRoomKey(String key) {
        Room room = roomDao.getRoomByKey(key);
        List<User> users = room.getUsers();
        users.forEach(user ->
                userService.deleteUserById(user.getUserId())
        );
    }

    private boolean checkIfRoomExist(String key) {
        Room room = roomDao.getRoomByKey(key);
        return ObjectUtils.isNotEmpty(room);
    }

}
