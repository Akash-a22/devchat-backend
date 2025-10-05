package com.devchat.devchat_room.service.impl;

import com.devchat.devchat_room.dao.RoomDao;
import com.devchat.devchat_room.dao.SessionDao;
import com.devchat.devchat_room.dao.UserDao;
import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.model.Room;
import com.devchat.devchat_room.model.Session;
import com.devchat.devchat_room.model.User;
import com.devchat.devchat_room.service.UserService;
import com.devchat.devchat_room.util.CommonConstants;
import com.devchat.devchat_room.util.CommonUtil;
import com.devchat.devchat_room.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private SessionDao sessionDao;

    @Override
    public ResponseDTO createUser(User user) {
        if (ObjectUtils.isNotEmpty(userDao.checkIfUserExistWithKey(user.getName(), user.getRoomId()))) {
            return commonUtil.prepareResponse(new ResponseDTO(), null, "NOT_CREATED", HttpStatus.BAD_REQUEST.value(), "User with the name is already exist in room");
        }
        user.setKey(commonUtil.generateKey());
        user.setUserId(commonUtil.getId("USER"));
        user.setToken(jwtUtil.generateToken(user.getName(), user.getUserId()));
        user.createEntity(user.getName());
        user.setKey(commonUtil.generateKey());
        userDao.saveUser(user);
        log.info("User with name {} is created successfully", user.getName());
        return commonUtil.prepareResponse(new ResponseDTO(), user, "CREATED", HttpStatus.CREATED.value(), "User is created successfully");
    }

    @Override
    public ResponseDTO deleteUserById(String id) {
        ResponseDTO response = getUserById(id);
        User user = (User) response.getResponseObject();
        if (ObjectUtils.isNotEmpty(user)) {
            removeUserFromRoom(user);
        }
        userDao.deleteUserById(id);
        return commonUtil.prepareResponse(new ResponseDTO(), null, "SUCCESS", HttpStatus.OK.value(), "User is removed from room successfully");
    }

    private void removeUserFromRoom(User user) {
        Room room = roomDao.getRoomByKey(CommonConstants.ROOM_ID, user.getRoomId());
        if (ObjectUtils.isNotEmpty(room)) {
            room.getUsers().removeIf(ur -> StringUtils.equalsIgnoreCase(ur.getUserId(), user.getUserId()));
            updateRoomSession(user.getRoomId());
            roomDao.updateRoom(room);
        }
    }

    private void updateRoomSession(String roomId) {
        Session session = sessionDao.getSession(roomId);
        LocalDateTime modifiedOn = session.getModifiedOn();
        session.setExpireOn(commonUtil.getModifiedOn(modifiedOn));
        sessionDao.updateSession(session);
    }

    @Override
    public ResponseDTO getUserById(String id) {
        User user = userDao.getUserById(id);
        if (ObjectUtils.isNotEmpty(user)) {
            return commonUtil.prepareResponse(new ResponseDTO(), user, "FOUND", HttpStatus.CREATED.value(), "User is found successfully");
        }
        return commonUtil.prepareResponse(new ResponseDTO(), null, "NOT_FOUND", HttpStatus.NO_CONTENT.value(), "User not found");
    }

    @Override
    public ResponseDTO getAllUser() {
        List<User> users = userDao.getAllUser();
        if (CollectionUtils.isEmpty(users)) {
            return commonUtil.prepareResponse(new ResponseDTO(), null, "NOT_FOUND", HttpStatus.NO_CONTENT.value(), "no user found ");
        }
        return commonUtil.prepareResponse(new ResponseDTO(), users, "FOUND", HttpStatus.OK.value(), "Users is found successfully");
    }

}
