package com.mbld.jigsly.service;

import com.mbld.jigsly.dto.RegistrationUserDto;
import com.mbld.jigsly.dto.UserDto;
import com.mbld.jigsly.exception.domain.EmailExistException;
import com.mbld.jigsly.exception.domain.UserNotFoundException;
import com.mbld.jigsly.exception.domain.UsernameExistException;
import com.mbld.jigsly.model.user.AbstractUser;
import com.mbld.jigsly.model.user.User;

public interface UserService {
    User register(RegistrationUserDto user) throws UsernameExistException, EmailExistException;
    User findUserByUsername(String username);
    AbstractUser findAbstractUserByUsername(String username);
    UserDto findUserDtoByUsername(String username);
    User findUserByEmail(String username);
    void removeUser(String username);
}
