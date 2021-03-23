package com.mbld.jigsly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGameRoomDto {
    Integer pieces;
    Integer maximum;
    String customImage;
    Integer selectedImage;
    Integer width;
    Integer height;
}
