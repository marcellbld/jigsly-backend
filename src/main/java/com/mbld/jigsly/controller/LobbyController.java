package com.mbld.jigsly.controller;

import com.mbld.jigsly.dto.CreateGameRoomDto;
import com.mbld.jigsly.dto.GameRoomDto;
import com.mbld.jigsly.exception.domain.RoomFullException;
import com.mbld.jigsly.model.message.JoinMessage;
import com.mbld.jigsly.model.room.GameRoom;
import com.mbld.jigsly.service.GameRoomService;
import com.mbld.jigsly.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

@Controller
public class LobbyController {

    private final GameRoomService gameRoomService;

    public LobbyController(GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }

    @PostMapping("/lobby/join-room")
    public ResponseEntity<Long> joinGameRoom(@RequestBody JoinMessage joinMessage) throws RoomFullException {
        gameRoomService.addUserToGameRoom(joinMessage.getUser().getUsername(), joinMessage.getRoomId());

        return ResponseEntity.ok(joinMessage.getRoomId());
    }

    @PostMapping("/lobby/create-room")
    public ResponseEntity<Long> createGameRoom(@RequestBody CreateGameRoomDto dto){
        GameRoom room = gameRoomService.createGameRoom(dto);

        return ResponseEntity.ok(room.getId());
    }

    @GetMapping("/lobby/rooms")
    public ResponseEntity<List<GameRoomDto>> getRooms(){
        return ResponseEntity.ok(gameRoomService.getGameRoomDtos());
    }
}
