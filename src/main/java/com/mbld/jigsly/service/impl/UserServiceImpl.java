package com.mbld.jigsly.service.impl;

import com.mbld.jigsly.converter.AbstractUserToUserDtoConverter;
import com.mbld.jigsly.dto.RegistrationUserDto;
import com.mbld.jigsly.dto.UserDto;
import com.mbld.jigsly.enumeration.Role;
import com.mbld.jigsly.exception.domain.EmailExistException;
import com.mbld.jigsly.exception.domain.UsernameExistException;
import com.mbld.jigsly.model.user.AbstractUser;
import com.mbld.jigsly.model.user.AnonymousUser;
import com.mbld.jigsly.model.user.User;
import com.mbld.jigsly.model.user.UserPrincipal;
import com.mbld.jigsly.repository.UserRepository;
import com.mbld.jigsly.service.AnonymousUserService;
import com.mbld.jigsly.service.LoginAttemptService;
import com.mbld.jigsly.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl  implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AbstractUserToUserDtoConverter abstractUserToUserDtoConverter;
    private final AnonymousUserService anonymousUserService;

    public UserServiceImpl(UserRepository userRepository, LoginAttemptService loginAttemptService, BCryptPasswordEncoder bCryptPasswordEncoder, AbstractUserToUserDtoConverter abstractUserToUserDtoConverter, AnonymousUserService anonymousUserService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.abstractUserToUserDtoConverter = abstractUserToUserDtoConverter;
        this.anonymousUserService = anonymousUserService;
    }

    @Override
    public User register(RegistrationUserDto regUser) throws UsernameExistException, EmailExistException {
        String username = regUser.getUsername();
        String email = regUser.getEmail();
        String password = regUser.getPassword();
        validateUsernameAndEmail(username, email);

        User user = new User();
        String encodedPassword = encodePassword(password);

        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        userRepository.save(user);
        return user;
    }

    private UserDto convertUserToUserDto(AbstractUser user){
        return abstractUserToUserDtoConverter.convert(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public AbstractUser findAbstractUserByUsername(String username) {
        User foundUser = findUserByUsername(username);
        AbstractUser anonUser = anonymousUserService.getAbstractUserByUsername(username);

        return foundUser == null ? anonUser : foundUser; //TODO
    }

    @Override
    public UserDto findUserDtoByUsername(String username) {
        return convertUserToUserDto(findAbstractUserByUsername(username));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null){
            log.error("No user found by username: "+username);
            throw new UsernameNotFoundException("No user found by username: "+username);
        } else {
            validateLoginAttempt(user);

            user.setLastLoginDate(new Date());
            userRepository.save(user);

            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info("Found user by username: " + username);

            return userPrincipal;
        }
    }

    public void removeUser(String username){
        if(username == null)
            return;

        anonymousUserService.removeUser(username);
    }

    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())){
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private User validateUsernameAndEmail(String username, String email) throws UsernameExistException, EmailExistException {
        User userByUsername = findUserByUsername(username);
        User userByEmail = findUserByEmail(email);

        if(userByUsername != null) {
            throw new UsernameExistException("Username already exists");
        }
        if(userByEmail != null){
            throw new EmailExistException("Email already exists");
        }
        return null;
    }
    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
