package com.mbld.jigsly.model.message;

import com.mbld.jigsly.dto.UserDto;
import com.mbld.jigsly.enumeration.JoinType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinMessage {
    private UserDto user;
    private Long roomId;
    private JoinType type;
    private String color;
}
