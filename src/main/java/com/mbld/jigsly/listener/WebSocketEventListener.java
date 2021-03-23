package com.mbld.jigsly.listener;

import com.mbld.jigsly.enumeration.JoinType;
import com.mbld.jigsly.model.message.JoinMessage;
import com.mbld.jigsly.service.BucketService;
import com.mbld.jigsly.service.GameRoomService;
import com.mbld.jigsly.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations sendingOperations;
    private final UserService userService;
    private final GameRoomService gameRoomService;
    private final BucketService bucketService;

    public WebSocketEventListener(SimpMessageSendingOperations sendingOperations, UserService userService, GameRoomService gameRoomService, BucketService bucketService) {
        this.sendingOperations = sendingOperations;
        this.userService = userService;
        this.gameRoomService = gameRoomService;
        this.bucketService = bucketService;
    }

    @EventListener
    public void handleWebSocketConnectListener(final SessionConnectedEvent event){
        log.info("New connection "+event.getUser());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event){
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        final String username = (String)headerAccessor.getSessionAttributes().get("username");
        final Long roomId = (Long)headerAccessor.getSessionAttributes().get("roomId");

        headerAccessor.getSessionAttributes().remove("username");
        headerAccessor.getSessionAttributes().remove("roomId");
        if(username == null)
            return;

        if(roomId != null){
            gameRoomService.removeUserFromGameRoom(username, roomId);

            final JoinMessage joinMessage = JoinMessage.builder()
                    .user(userService.findUserDtoByUsername(username))
                    .type(JoinType.DISCONNECTED).build();

            sendingOperations.convertAndSend("/topic/game/"+roomId+"/join", joinMessage);
        }

        bucketService.removeBucket(username);
        userService.removeUser(username);
    }


}
