package com.mbld.jigsly.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private boolean isActive;
    private String role;
    private String[] authorities;
}
