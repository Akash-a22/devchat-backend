package com.devchat.devchat_room.controller;

import com.devchat.devchat_room.model.ResponseDTO;
import com.devchat.devchat_room.model.Room;
import com.devchat.devchat_room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseDTO createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

    @PostMapping("/update")
    public ResponseDTO updateRoom(@RequestBody Room room) {
        return roomService.updateRoom(room);
    }

    @PostMapping("/join")
    public ResponseDTO joinRoom(@RequestBody Room room) {
        return roomService.joinRoom(room);
    }

    @DeleteMapping("/{key}")
    public ResponseDTO deleteRoom(@PathVariable String key) {
        return roomService.deleteRoom(key);
    }
}
