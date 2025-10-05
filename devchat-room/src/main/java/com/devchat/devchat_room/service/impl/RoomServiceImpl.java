package com.devchat.devchat_room.service.impl;

import com.devchat.devchat_room.dao.RoomDao;
import com.devchat.devchat_room.dao.SessionDao;
import com.devchat.devchat_room.model.*;
import com.devchat.devchat_room.service.RoomService;
import com.devchat.devchat_room.service.UserService;
import com.devchat.devchat_room.util.CommonConstants;
import com.devchat.devchat_room.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
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

    @Autowired
    private SessionDao sessionDao;

    @Override
    public ResponseDTO createRoom(Room room) {
        HashSet<String> response = validateRoom(room);
        if (!CollectionUtils.isEmpty(response)) {
            return commonUtil.prepareResponse(new ResponseDTO(), response, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "fields are not valid");
        }
        String key = commonUtil.generateKey();
        room.setKey(key);
        room.setRoomId(commonUtil.getId("ROOM"));
        room.setCreatedOn(LocalDateTime.now());
        room.setModifiedOn(LocalDateTime.now());
        room.setSize(room.getSize());

        // session for room
        addSessionForRoom(room);

        User user = room.getUsers().get(0);
        user.setKey(commonUtil.generateKey());
        user.setRoomId(room.getRoomId());


        room.createEntity(user.getName());
        ResponseDTO responseDTO = userService.createUser(user);
        if(ObjectUtils.isEmpty(responseDTO.getResponseObject())){
            return responseDTO;
        }
        roomDao.createRoom(room);
        return commonUtil.prepareResponse(new ResponseDTO(), room, "CREATED", HttpStatus.CREATED.value(), "Room is created successfully");
    }

    private void addSessionForRoom(Room room) {
        Session session = new Session();
        session.setCreateOn(LocalDateTime.now());
        session.setModifiedOn(LocalDateTime.now());
        session.setExpireOn(commonUtil.getModifiedOn(session.getModifiedOn()));
        session.setRoomId(room.getRoomId());
        session.setStatus(SessionStatus.ACTIVE);
        session.setId(commonUtil.generateKey());
        sessionDao.saveSession(session);
    }



    private HashSet<String> validateRoom(Room room) {
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
        if (room.getSize() == 0 || room.getSize() > 5 || room.getUsers().size() == 5) {
            response.add("size is empty or size is more than 5");
        }
        return response;
    }

    @Override
    public ResponseDTO deleteRoom(String roomId) {
        if (StringUtils.isBlank(roomId)) {
            return commonUtil.prepareResponse(new ResponseDTO(), null, "NOT_FOUND", HttpStatus.NO_CONTENT.value(), "roomId is blank");
        }
        if (checkIfRoomExist(roomId)) {
            deleteUserByRoomKey(roomId);
            roomDao.deleteRoom(roomId);
            sessionDao.deleteSessionByRoomId(roomId);
            return commonUtil.prepareResponse(new ResponseDTO(), null, "DELETED", HttpStatus.OK.value(), "Room is deleted successfully");
        }
        return commonUtil.prepareResponse(new ResponseDTO(), null, "NOT_FOUND", HttpStatus.NO_CONTENT.value(), "Room doesn't exits");
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
        Room roomDB = roomDao.getRoomByKey(CommonConstants.KEY,roomKey);
        User user = room.getUsers().get(0);
        if(isUserExitInRoom(user.getName(),roomKey)){
            return commonUtil.prepareResponse(new ResponseDTO() , null , "BAD_REQUEST" , HttpStatus.BAD_REQUEST.value(), "User with the name is already present in room , provide any other name");
        }
        user.setRoomId(roomDB.getRoomId());
        roomDB.updateEntity(user.getName());
        userService.createUser(user);
        roomDB.getUsers().add(user);
        updateRoomSession(roomDB.getRoomId());
        roomDao.updateRoom(roomDB);
        roomDB.setUsers(Collections.singletonList(user));

        return commonUtil.prepareResponse(new ResponseDTO(), roomDB, "JOINED", HttpStatus.OK.value(), "User joined room successfully");
    }

    @Override
    public ResponseDTO getRoom(String roomId, String userId) {
        Room room = roomDao.getRoomByKey(CommonConstants.ROOM_ID, roomId);
        if(ObjectUtils.isEmpty(room)){
            return commonUtil.prepareResponse(new ResponseDTO(), null, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Room is not exist");
        }
        HashSet<String> response = validJoinRoom(room);
        if (!CollectionUtils.isEmpty(response)) {
            return commonUtil.prepareResponse(new ResponseDTO(), response, "BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Please provide necessary fields");
        }
        prepareRoomResponse(room , userId);
        return  commonUtil.prepareResponse(new ResponseDTO(), room, "SUCCESS", HttpStatus.OK.value(), "");
    }

    private void prepareRoomResponse(Room room, String userId) {
        room.getUsers().forEach(user -> {
                    if (!StringUtils.equalsIgnoreCase(userId, user.getUserId())) {
                        user.setToken(StringUtils.EMPTY);
                    }
                }
        );
    }

    private void updateRoomSession(String roomId) {
        Session session = sessionDao.getSession(roomId);
        LocalDateTime modifiedOn = session.getModifiedOn();
        session.setExpireOn(commonUtil.getModifiedOn(modifiedOn));
        sessionDao.updateSession(session);
    }

    private boolean isUserExitInRoom(String userName, String roomKey) {
        //same name user can't join room
        Room room = roomDao.getRoomByKey(CommonConstants.KEY, roomKey);
        if(ObjectUtils.isNotEmpty(room) && !CollectionUtils.isEmpty(room.getUsers())){
            return room.getUsers().stream().anyMatch(u -> StringUtils.equalsIgnoreCase(u.getName() , userName));
        }
        return false;
    }

    private HashSet<String> validJoinRoom(Room room) {
        HashSet<String> response = new HashSet<>();
        if (CollectionUtils.isEmpty(room.getUsers())) {
            response.add("user or users are empty");
        }
        Room dbRoom = roomDao.getRoomByKey(CommonConstants.KEY, room.getKey());
        if (ObjectUtils.isEmpty(dbRoom)) {
            response.add("room is not found with current key or its already closed");
            return response;
        }
        if (dbRoom.getUsers().size() > dbRoom.getSize()) {
            response.add("room is full");
            return response;
        }
        // is room Session ACTIVE
        Session session = sessionDao.getSession(room.getRoomId());
        if(ObjectUtils.isNotEmpty(session) && (StringUtils.equalsIgnoreCase(session.getStatus().name() , SessionStatus.EXPIRED.name()) || session.getExpireOn().isBefore(LocalDateTime.now()))){
            response.add("Room session is expired , please create new room");
        }
        return response;
    }

    private void deleteUserByRoomKey(String key) {
        Room room = roomDao.getRoomByKey(CommonConstants.KEY,key);
        List<User> users = room.getUsers();
        users.forEach(user ->
                userService.deleteUserById(user.getUserId())
        );
    }

    private boolean checkIfRoomExist(String key) {
        Room room = roomDao.getRoomByKey(CommonConstants.KEY, key);
        return ObjectUtils.isNotEmpty(room);
    }

}
