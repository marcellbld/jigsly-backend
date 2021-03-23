package com.mbld.jigsly.controller;

import com.mbld.jigsly.dto.GameRoomDto;
import com.mbld.jigsly.enumeration.JoinType;
import com.mbld.jigsly.enumeration.PieceAttachType;
import com.mbld.jigsly.model.message.JoinMessage;
import com.mbld.jigsly.model.message.PieceAttachMessage;
import com.mbld.jigsly.model.message.PieceMoveMessage;
import com.mbld.jigsly.model.puzzle.Puzzle;
import com.mbld.jigsly.model.puzzle.PuzzlePiece;
import com.mbld.jigsly.service.GameRoomService;
import com.mbld.jigsly.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Controller
public class RoomController {

    private final GameRoomService gameRoomService;
    private final UserService userService;
    private final SimpMessageSendingOperations sendingOperations;

    public RoomController(GameRoomService gameRoomService, UserService userService, SimpMessageSendingOperations sendingOperations) {
        this.gameRoomService = gameRoomService;
        this.userService = userService;
        this.sendingOperations = sendingOperations;
    }

    @MessageMapping("/game/leave")
    public void leaveGameRoom(@RequestBody JoinMessage joinMessage, SimpMessageHeaderAccessor headerAccessor){
        String username = (String)headerAccessor.getSessionAttributes().get("username");
        final Long roomId = (Long)headerAccessor.getSessionAttributes().get("roomId");

        headerAccessor.getSessionAttributes().remove("username");
        headerAccessor.getSessionAttributes().remove("roomId");

        if(roomId != null){
            gameRoomService.removeUserFromGameRoom(username, roomId);
        }

        sendingOperations.convertAndSend("/topic/game/"+roomId+"/join", joinMessage);
    }

    @MessageMapping("/game/join")
    public void joinGameRoom(@RequestBody JoinMessage joinMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String)headerAccessor.getSessionAttributes().get("username");
        Long roomId = joinMessage.getRoomId();

        JoinType joinType = joinMessage.getType();
        if(joinType.equals(JoinType.CONNECTED)) {
            headerAccessor.getSessionAttributes().put("roomId", roomId);

            joinMessage.setUser(userService.findUserDtoByUsername(username));
            joinMessage.setColor(gameRoomService.getGameRoom(roomId).getUserColor(userService.findAbstractUserByUsername(username)).getColorCode());
        }
        else if(joinType.equals(JoinType.DISCONNECTED)){
            roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
            joinMessage.setRoomId(roomId);
            joinMessage.setUser(userService.findUserDtoByUsername(username));

            gameRoomService.removeUserFromGameRoom(username, roomId);
            headerAccessor.getSessionAttributes().remove("roomId");
        }
        sendingOperations.convertAndSend("/topic/game/" + roomId + "/join", joinMessage);
    }

    @MessageMapping("/game/piece/attach")
    public void attachPiece(@RequestBody PieceAttachMessage message, SimpMessageHeaderAccessor headerAccessor){
        String username = (String)headerAccessor.getSessionAttributes().get("username");
        Long roomId = (Long)headerAccessor.getSessionAttributes().get("roomId");
        message.setUsername(username);

        if(message.getType().equals(PieceAttachType.DETACHED) && roomId != null){
            gameRoomService.getPuzzle(roomId).getPuzzlePieces()[message.getIdX()][message.getIdY()].checkRealPosition();
            if(gameRoomService.getPuzzle(roomId).getPuzzlePieces()[message.getIdX()][message.getIdY()].isFoundRealPosition()){
                System.out.println("FOUND REAL POSITION: "+gameRoomService.getPuzzle(roomId).getPuzzlePieces()[message.getIdX()][message.getIdY()].getX() + " "
                +gameRoomService.getPuzzle(roomId).getPuzzlePieces()[message.getIdX()][message.getIdY()].getY());
                System.out.println("FOUND REAL POSITION REAL: "+gameRoomService.getPuzzle(roomId).getPuzzlePieces()[message.getIdX()][message.getIdY()].getRealX() + " "
                        +gameRoomService.getPuzzle(roomId).getPuzzlePieces()[message.getIdX()][message.getIdY()].getRealY());
            }
            gameRoomService.getPuzzle(roomId).detachPiece(message.getIdX(), message.getIdY());
            Set<PuzzlePiece> found = gameRoomService.getPuzzle(roomId).getPiecesInGroupByCoordinate(message.getIdX(), message.getIdY());
            found.add(gameRoomService.getPuzzle(roomId).getPuzzlePieces()[message.getIdX()][message.getIdY()]);
            message.setJoinedPieces(found.toArray(new PuzzlePiece[0]));
        }

        sendingOperations.convertAndSend("/topic/game/"+roomId+"/attach", message);
    }


    @MessageMapping("/game/piece/move")
    public void movePiece(@RequestBody PieceMoveMessage message, SimpMessageHeaderAccessor headerAccessor){
        Long roomId = (Long)headerAccessor.getSessionAttributes().get("roomId");
        gameRoomService.movePuzzlePiece(roomId, message.getIdX(), message.getIdY(), message.getX(), message.getY());

        sendingOperations.convertAndSend("/topic/game/"+roomId+"/move", message);
    }

    @GetMapping("/game/{roomId}")
    public ResponseEntity<GameRoomDto> getGameRoomData(@PathVariable("roomId") String roomId){
        return ResponseEntity.ok(gameRoomService.getGameRoomDto(Long.parseLong(roomId)));
    }

    @GetMapping("/game/{roomId}/puzzle")
    public ResponseEntity<Puzzle> getPuzzleData(@PathVariable("roomId") String roomId){
        return ResponseEntity.ok(gameRoomService.getPuzzle(Long.parseLong(roomId)));
    }
}
