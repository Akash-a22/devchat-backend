package com.devchat.devchat_room.dao;

import com.devchat.devchat_room.model.Session;

public interface SessionDao {
    void saveSession(Session session);

    Session getSession(String roomId);

    void updateSession(Session session);

    void deleteSessionByRoomId(String roomId);
}
