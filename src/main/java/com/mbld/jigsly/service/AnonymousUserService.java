package com.mbld.jigsly.service;

import com.mbld.jigsly.model.user.AbstractUser;
import com.mbld.jigsly.model.user.AnonymousUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnonymousUserService {
    private final List<AnonymousUser> users;

    public AnonymousUserService(){
        users = new ArrayList<>();
    }

    public void addUser(String username){
        users.add(new AnonymousUser(username));
    }
    public void removeUser(String username){
        users.removeIf(u -> u.getUsername().equals(username));
        System.out.println("Removed from anonUserService: "+username);
        System.out.println(users.size());
    }

    public AbstractUser getAbstractUserByUsername(String username){
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElseThrow();
    }
}
