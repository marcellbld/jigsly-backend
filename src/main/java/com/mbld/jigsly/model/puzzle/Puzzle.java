package com.mbld.jigsly.model.puzzle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mbld.jigsly.constant.PuzzleConstant;
import com.mbld.jigsly.enumeration.PuzzlePieceSideType;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Puzzle {

    @JsonIgnore
    private final HashMap<Integer, Set<PuzzlePiece>> groupMap;

    private final float puzzleWidth;
    private final float puzzleHeight;

    private final float pieceWidth;
    private final float pieceHeight;

    private final int piecesX;
    private final int piecesY;

    private final PuzzlePiece[][] puzzlePieces;

    private final String imageBase64;

    private final int worldWidth;
    private final int worldHeight;

    public Puzzle(int pieces, PuzzleImage baseImage) {
        this.puzzleWidth = baseImage.getWidth();
        this.puzzleHeight = baseImage.getHeight();

        this.worldWidth = (int)(puzzleWidth*PuzzleConstant.WORLD_WIDTH_OFFSET_MULTIPLIER);
        this.worldHeight = (int)(puzzleHeight*PuzzleConstant.WORLD_WIDTH_OFFSET_MULTIPLIER);

        int piecesX = (int)(Math.sqrt(pieces)*((double)puzzleWidth/puzzleHeight));
        int piecesY = (int)(Math.sqrt(pieces)*((double)puzzleHeight/puzzleWidth));

        this.piecesX = piecesX;
        this.piecesY =  piecesY;
        this.pieceWidth = puzzleWidth/piecesX;
        this.pieceHeight = puzzleHeight/piecesY;
        this.groupMap = new HashMap<>();
        this.imageBase64 = baseImage.getImageBase64();

        this.puzzlePieces = new PuzzlePiece[piecesY][piecesX];
        int count = 0;
        for(int i = 0; i < piecesY; i++){
            for(int j = 0; j < piecesX; j++){
                PuzzlePiece piece = new PuzzlePiece(this, i, j, count);
                groupMap.put(count++, new HashSet<>(Collections.singletonList(piece)));

                float lowerX = (float)worldWidth/2f-puzzleWidth+pieceWidth/2;
                float upperX = (float)worldWidth/2f-puzzleWidth/2f-pieceWidth*2;
                float lowerY = (float)worldHeight/2-puzzleHeight/2-puzzleHeight/6-pieceHeight/2;
                float upperY = (float)worldHeight/2+puzzleHeight/2+puzzleHeight/6-pieceHeight/2;

                float randX = ((float)Math.random() * (upperX - lowerX)) + lowerX;
                float randY = ((float)Math.random() * (upperY - lowerY)) + lowerY;

                piece.setRealPosition(worldWidth/2f-puzzleWidth/2f-pieceWidth/4+j*pieceWidth,
                        worldHeight/2f-puzzleHeight/2f-pieceHeight/4+i*pieceHeight);

                piece.setPosition(randX+(Math.random() < 0.5 ? worldWidth/2f : 0), randY);
                if(j > 0){
                    PuzzlePiece left = puzzlePieces[i][j-1];
                    left.setPuzzlePieceToSide(PuzzlePieceSideType.RIGHT, piece);
                    piece.setPuzzlePieceToSide(PuzzlePieceSideType.LEFT, left);
                }
                if(i > 0){
                    PuzzlePiece top = puzzlePieces[i-1][j];
                    top.setPuzzlePieceToSide(PuzzlePieceSideType.BOTTOM, piece);
                    piece.setPuzzlePieceToSide(PuzzlePieceSideType.TOP, top);
                }
                puzzlePieces[i][j] = piece;
            }
        }
    }
    public void setPuzzlePieceGroup(PuzzlePiece piece, int newGroup){
        int oldGroup = piece.getGroup();
        if(oldGroup == newGroup)
            return;

        if(groupMap.containsKey(newGroup)){
            groupMap.get(oldGroup).forEach(p -> p.setGroup(newGroup));
            groupMap.get(newGroup).addAll(groupMap.get(oldGroup));
            groupMap.get(oldGroup).clear();
        }
    }
    public Set<PuzzlePiece> getPiecesInGroupByCoordinate(int idX, int idY){
        return groupMap.get(puzzlePieces[idX][idY].getGroup());
    }
    public Set<PuzzlePiece> getPiecesInGroup(int groupId){
        return groupMap.get(groupId);
    }

    public void movePiece(int idX, int idY, float x, float y){
        PuzzlePiece piece = puzzlePieces[idX][idY];
        piece.setPosition(x, y);
    }
    public void detachPiece(int idX, int idY){

        checkJoins(idX, idY);
    }
    private void checkJoins(int idX, int idY){
        puzzlePieces[idX][idY].checkJoins();
    }
}
