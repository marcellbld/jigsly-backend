package com.mbld.jigsly.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AnonymousUser extends AbstractUser{
    private String username;
}
