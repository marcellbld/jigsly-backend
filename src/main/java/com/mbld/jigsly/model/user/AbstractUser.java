package com.mbld.jigsly.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public abstract class AbstractUser {
    private String username;
    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
}
