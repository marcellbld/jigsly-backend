package com.mbld.jigsly.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GameRoomDto {

    @NonNull
    private Long id;
    private List<UserDto> users;
    private Date created;
    private Integer maximumUsers;
    private String thumbnailBase64;
    private Integer pieces;
    private Map<String, String> userColors;


}
