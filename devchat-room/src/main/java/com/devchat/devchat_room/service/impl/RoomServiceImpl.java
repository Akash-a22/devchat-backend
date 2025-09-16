package com.devchat.devchat_room.service.impl;

import com.devchat.devchat_room.dao.RoomDao;
import com.devchat.devchat_room.dao.UserDao;
import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.model.Room;
import com.devchat.devchat_room.model.User;
import com.devchat.devchat_room.service.RoomService;
import com.devchat.devchat_room.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public ResponseDTO createRoom(Room room) {
        String key = generateKey();
        room.setKey(key);
        room.setCreatedOn(new Date());
        room.setModifiedOn(new Date());
        room.setPersonCount(1);
        User user = room.getUsers().get(0);
        user.setKey(generateKey());
        user.setId(UUID.randomUUID().toString());
        Room createdRoom = roomDao.createRoom(room);
        return commonUtil.prepareResponse(new ResponseDTO(), createdRoom ,"Created" , HttpStatus.CREATED.value(), "Room is created successfully");
    }

    @Override
    public ResponseDTO deleteRoom(String key) {
        if(StringUtils.isBlank(key)){
            return commonUtil.prepareResponse(new ResponseDTO(), null , "NOT_FOUND", HttpStatus.NO_CONTENT.value(), "Please send correct key");
        }
        if(checkIfRoomExist(key)){
            deleteUserByRoomKey(key);
            roomDao.deleteRoom(key);
            return commonUtil.prepareResponse(new ResponseDTO(),null,"DELETED", HttpStatus.OK.value(), "Room is deleted successfully");
        }
        return commonUtil.prepareResponse(new ResponseDTO(),null,"NOT_FOUND", HttpStatus.NO_CONTENT.value(), "Room with key doesn't exits");
    }

    @Override
    public ResponseDTO updateRoom(Room room) {
        //needtowork
        return null;
    }

    @Override
    public ResponseDTO joinRoom(Room room) {
        String roomKey = room.getKey();
        Room roomDB = roomDao.getRoomByKey(roomKey);
        roomDB.setModifiedOn(new Date());
        User user = room.getUsers().get(0);
        if(roomDB.getPersonCount() <= 5 ){
            roomDB.getUsers().add(user);
            roomDB.setPersonCount(roomDB.getPersonCount() + 1);
//            createUser(user);
            roomDao.updateRoom(roomDB);
        }
        return commonUtil.prepareResponse(new ResponseDTO(),null,"BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Room reached its max limit of participants");
    }

    private void deleteUserByRoomKey(String key) {
        Room room = roomDao.getRoomByKey(key);
        List<User> users = room.getUsers();
        users.forEach(user ->
            userDao.deleteUserById(user.getId())
        );
    }

    private boolean checkIfRoomExist(String key) {
        Room room = roomDao.getRoomByKey(key);
        return ObjectUtils.isNotEmpty(room);
    }

    private String generateKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
