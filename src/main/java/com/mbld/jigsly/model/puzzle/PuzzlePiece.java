package com.mbld.jigsly.model.puzzle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.mbld.jigsly.enumeration.PuzzlePieceSideType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

@Getter
public class PuzzlePiece {

    @JsonIgnore
    private final Puzzle puzzle;

    @JsonIgnore
    private final HashMap<PuzzlePieceSideType, PuzzlePiece> pieceSides;
    @JsonIgnore
    private final HashSet<PuzzlePieceSideType> foundSides;

    @JsonIgnore
    private float realX;
    @JsonIgnore
    private float realY;

    @Setter
    private int group;

    @JsonView(Puzzle.class)
    private final int idX;
    @JsonView(Puzzle.class)
    private final int idY;

    private boolean foundRealPosition = false;

    private float x;
    private float y;

    public PuzzlePiece(Puzzle puzzle, int idX, int idY, int group) {
        this.puzzle = puzzle;
        this.pieceSides = new HashMap<>();
        this.foundSides = new HashSet<>();
        this.idX = idX;
        this.idY = idY;
        this.group = group;
    }

    public void setPuzzlePieceToSide(PuzzlePieceSideType type, PuzzlePiece piece){
        pieceSides.put(type, piece);
    }

    private void setPositionRelativeTo(PuzzlePiece relative) {
        final float HEIGHT = puzzle.getPieceHeight();
        final float WIDTH = puzzle.getPieceWidth();

        this.x = relative.x + (this.idY-relative.idY)*WIDTH;
        this.y = relative.y + (this.idX-relative.idX)*HEIGHT;
    }
    public void setRealPosition(float x, float y){
        this.realX = x;
        this.realY = y;
    }
    public void setPosition(float x, float y) {
        if(this.foundRealPosition)
            return;

        this.x = x;
        this.y = y;

        final float HEIGHT = puzzle.getPieceHeight();
        final float WIDTH = puzzle.getPieceWidth();

        puzzle.getPiecesInGroup(group).forEach(piece ->{

            piece.x = this.x + (piece.idY-this.idY)*WIDTH;
            piece.y = this.y + (piece.idX-this.idX)*HEIGHT;
        });
    }

    public void checkRealPosition(){
        if(this.foundRealPosition)
            return;

        if(checkFoundRealPosition()){
            this.setPosition(realX, realY);
            puzzle.getPiecesInGroup(group).forEach(piece ->{
                piece.setPositionRelativeTo(this);
                piece.foundRealPosition = true;
            });
            this.foundRealPosition = true;
        }
    }
    public void checkJoins(){
        Queue<PuzzlePiece> pieces = new LinkedList<>();
        pieces.addAll(puzzle.getPiecesInGroup(group));
        pieces.add(this);

        boolean foundNew;
        PuzzlePiece before = null;
        while(!pieces.isEmpty()){
            PuzzlePiece piece = pieces.poll();
            foundNew = piece.checkJoinsOnThis();
            if(foundNew) {
                piece.setPositionRelativeTo(piece.pieceSides.get(piece.foundSides.stream().findFirst().orElseThrow()));
                pieces.addAll(puzzle.getPiecesInGroup(this.group));
                before = piece;
            }

            if(before != null){
                piece.setPositionRelativeTo(before);
                before = piece;
            }
        }
    }

    private boolean checkJoinsOnThis(){
        if(foundSides.size() == pieceSides.size())
            return false;

        int sizeBeforeSearch = foundSides.size();
        pieceSides.forEach((type, piece) -> {

            if(!foundSides.contains(type)){
                if(checkJoin(type,piece)){
                    foundSides.add(type);
                    switch (type){
                        case TOP:
                            piece.foundSides.add(PuzzlePieceSideType.BOTTOM);
                            break;
                        case BOTTOM:
                            piece.foundSides.add(PuzzlePieceSideType.TOP);
                            break;
                        case LEFT:
                            piece.foundSides.add(PuzzlePieceSideType.RIGHT);
                            break;
                        case RIGHT:
                            piece.foundSides.add(PuzzlePieceSideType.LEFT);
                            break;
                    }
                    puzzle.setPuzzlePieceGroup(this, piece.group);
                }
            }
        });
        return sizeBeforeSearch < foundSides.size();
    }
    private boolean checkFoundRealPosition(){
        float offsetX = this.puzzle.getPieceWidth()*0.1f;
        float offsetY = this.puzzle.getPieceHeight()*0.1f;
        return Math.abs(this.x-this.realX) < offsetX &&
                Math.abs(this.y-this.realY) < offsetY;
    }

    private boolean checkJoin(PuzzlePieceSideType type, PuzzlePiece piece){

        float offsetX = this.puzzle.getPieceWidth()*0.1f;
        float offsetY = this.puzzle.getPieceHeight()*0.1f;
        final float HEIGHT = puzzle.getPieceHeight();
        final float WIDTH = puzzle.getPieceWidth();
        switch (type){
            case TOP:
                return (Math.abs(piece.x-this.x) < offsetX && Math.abs(piece.y-this.y+HEIGHT) < offsetY);
            case BOTTOM:
                return (Math.abs(piece.x-this.x) < offsetX && Math.abs(piece.y-this.y-HEIGHT) < offsetY);
            case LEFT:
                return (Math.abs(piece.x-this.x+WIDTH) < offsetX && Math.abs(piece.y-this.y) < offsetY);
            case RIGHT:
                return (Math.abs(piece.x-this.x-WIDTH) < offsetX && Math.abs(piece.y-this.y) < offsetY);
        }
        return false;
    }
}
