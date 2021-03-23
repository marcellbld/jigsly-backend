package com.mbld.jigsly.model.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mbld.jigsly.enumeration.UserColor;
import com.mbld.jigsly.model.puzzle.Puzzle;
import com.mbld.jigsly.model.puzzle.PuzzleImage;
import com.mbld.jigsly.model.user.AbstractUser;
import lombok.Getter;

import java.util.*;

@Getter
public class GameRoom {

    private final Long id;
    private final List<AbstractUser> users;
    private final Date created;
    private final Integer maximumUsers;
    private final HashMap<UserColor, AbstractUser> userColors;

    @JsonIgnore
    private Puzzle puzzle;

    public GameRoom(Long id, Integer maximumUsers, Date created, PuzzleImage baseImage, int pieces){
        this.id = id;
        this.users = new ArrayList<>();
        this.maximumUsers = maximumUsers;
        this.created = created;

        this.puzzle = new Puzzle(pieces, baseImage);
        this.userColors = new HashMap<>();
    }
    public void addUser(AbstractUser user){
        users.add(user);
        for(UserColor color : UserColor.values()){
            if(!userColors.containsKey(color)){
                userColors.put(color, user);
                break;
            }
        }
    }
    public boolean isFull(){
        return this.users.size() >= this.maximumUsers;
    }
    public UserColor getUserColor(AbstractUser user){
        return userColors.entrySet()
                .stream()
                .filter(entry -> user.getUsername().equals(entry.getValue().getUsername()))
                .map(Map.Entry::getKey).findFirst().orElse(null);
    }
    public void removeUser(AbstractUser user){
        users.removeIf(abstractUser -> abstractUser.getUsername().equals(user.getUsername()));

        userColors.remove(getUserColor(user));
    }
}
