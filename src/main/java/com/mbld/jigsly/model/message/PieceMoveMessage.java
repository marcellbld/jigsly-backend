package com.mbld.jigsly.model.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PieceMoveMessage {
    private float x;
    private float y;
    private Integer idX;
    private Integer idY;
    private Integer group;
}
