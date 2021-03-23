package com.mbld.jigsly.model.message;

import com.mbld.jigsly.enumeration.PieceAttachType;
import com.mbld.jigsly.model.puzzle.PuzzlePiece;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PieceAttachMessage {
    private String username;
    private PieceAttachType type;
    private Integer idX;
    private Integer idY;
    private PuzzlePiece[] joinedPieces;
}
