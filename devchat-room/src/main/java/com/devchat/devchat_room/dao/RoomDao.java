package com.devchat.devchat_room.dao;


import com.devchat.devchat_room.model.Room;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public interface RoomDao {

    Room createRoom(Room room);

    Room getRoomByKey(String key);

    DeleteResult deleteRoom(String key);

    UpdateResult updateRoom(Room roomDB);
}
