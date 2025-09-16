package com.devchat.devchat_room.service;


import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.model.Room;

public interface RoomService {


    ResponseDTO createRoom(Room room);

    ResponseDTO deleteRoom(String key);

    ResponseDTO updateRoom(Room room);

    ResponseDTO joinRoom(Room room);
}
