package com.mbld.jigsly.converter;

import com.mbld.jigsly.dto.GameRoomDto;
import com.mbld.jigsly.enumeration.UserColor;
import com.mbld.jigsly.model.room.GameRoom;
import com.mbld.jigsly.model.user.AbstractUser;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GameRoomToGameRoomDtoConverter implements Converter<GameRoom, GameRoomDto> {

    private final AbstractUserToUserDtoConverter abstractUserToUserDtoConverter;

    public GameRoomToGameRoomDtoConverter(AbstractUserToUserDtoConverter abstractUserToUserDtoConverter) {
        this.abstractUserToUserDtoConverter = abstractUserToUserDtoConverter;
    }

    @Synchronized
    @Nullable
    @Override
    public GameRoomDto convert(GameRoom gameRoom) {

        final GameRoomDto gameRoomDto = new GameRoomDto();

        gameRoomDto.setId(gameRoom.getId());
        gameRoomDto.setCreated(gameRoom.getCreated());
        gameRoomDto.setMaximumUsers(gameRoom.getMaximumUsers());
        gameRoomDto.setUsers(gameRoom.getUsers().stream().map(abstractUserToUserDtoConverter::convert).collect(Collectors.toList()));
        gameRoomDto.setPieces(gameRoom.getPuzzle().getPiecesX()*gameRoom.getPuzzle().getPiecesY());
        gameRoomDto.setThumbnailBase64(gameRoom.getPuzzle().getImageBase64());

        HashMap<String, String> userColorsByUsername = new HashMap<>();

        for(Map.Entry<UserColor, AbstractUser> entry : gameRoom.getUserColors().entrySet()){
            userColorsByUsername.put(entry.getValue().getUsername(), entry.getKey().getColorCode());
        }
        gameRoomDto.setUserColors(userColorsByUsername);

        return gameRoomDto;
    }
}
