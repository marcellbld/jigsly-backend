package com.mbld.jigsly.service;

import com.mbld.jigsly.constant.ImageConstant;
import com.mbld.jigsly.converter.GameRoomToGameRoomDtoConverter;
import com.mbld.jigsly.dto.CreateGameRoomDto;
import com.mbld.jigsly.dto.GameRoomDto;
import com.mbld.jigsly.exception.domain.RoomFullException;
import com.mbld.jigsly.model.puzzle.Puzzle;
import com.mbld.jigsly.model.puzzle.PuzzleImage;
import com.mbld.jigsly.model.room.GameRoom;
import com.mbld.jigsly.model.user.AbstractUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class GameRoomService {

    private final UserService userService;
    private final PuzzleImageService puzzleImageService;

    private final AtomicLong gameRoomIdGenerator;
    private final GameRoomToGameRoomDtoConverter gameRoomToGameRoomDtoConverter;

    private final List<GameRoom> gameRooms;

    public GameRoomService(UserService userService, PuzzleImageService puzzleImageService, GameRoomToGameRoomDtoConverter gameRoomToGameRoomDtoConverter) {
        this.userService = userService;
        this.puzzleImageService = puzzleImageService;
        this.gameRoomToGameRoomDtoConverter = gameRoomToGameRoomDtoConverter;
        this.gameRooms = new ArrayList<>();
        this.gameRoomIdGenerator = new AtomicLong();

        createGameRoom(2, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[0]),300 );
        createGameRoom(3, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[1]),1000 );
        createGameRoom(2, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[2]),50 );
        createGameRoom(4, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[3]),100 );
        createGameRoom(5, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[4]),20 );
        createGameRoom(1, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[5]),250 );
        createGameRoom(2, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[6]),20 );
        createGameRoom(2, puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[7]),50 );

    }

    public List<GameRoomDto> getGameRoomDtos(){
        return gameRooms.stream().map(this::convertGameRoomToGameRoomDto).collect(Collectors.toList());
    }


    public GameRoom createGameRoom(CreateGameRoomDto dto){
        PuzzleImage img;
        if(dto.getSelectedImage() == -1){
            img = puzzleImageService.createPuzzleImageFromBase64(dto.getCustomImage(), dto.getWidth(), dto.getHeight());
        } else {
            img = puzzleImageService.getPuzzleImage(ImageConstant.DEFAULT_IMAGES[dto.getSelectedImage()]);
        }

        return createGameRoom(dto.getMaximum(), img, dto.getPieces());
    }

    public GameRoom createGameRoom(int maximum, PuzzleImage img, int pieces){
        GameRoom room = new GameRoom(gameRoomIdGenerator.getAndIncrement(), maximum, new Date(), img, pieces);
        gameRooms.add(room);
        return room;
    }
    public GameRoom getGameRoom(Long id) throws NoSuchElementException {
        return gameRooms.stream().filter(room -> room.getId().equals(id)).findFirst().orElseThrow();
    }

    public GameRoomDto getGameRoomDto(Long id) throws NoSuchElementException {
        return convertGameRoomToGameRoomDto(getGameRoom(id));
    }

    public void movePuzzlePiece(Long roomId, int idX, int idY, float x, float y){
        getPuzzle(roomId).movePiece(idX, idY, x, y);
    }

    private GameRoomDto convertGameRoomToGameRoomDto(GameRoom gameRoom){
        return gameRoomToGameRoomDtoConverter.convert(gameRoom);
    }

    public Puzzle getPuzzle(Long id){
        return getGameRoom(id).getPuzzle();
    }

    public void removeUserFromGameRoom(String username, Long roomId){
        AbstractUser user = userService.findAbstractUserByUsername(username);
        getGameRoom(roomId).removeUser(user);
    }

    public AbstractUser addUserToGameRoom(String username, Long roomId) throws NoSuchElementException{
        GameRoom room = getGameRoom(roomId);
        AbstractUser user = userService.findAbstractUserByUsername(username);

        if(room.isFull()){
            throw new IllegalStateException("This room is full");
        }

        room.addUser(user);
        return user;
    }
}
