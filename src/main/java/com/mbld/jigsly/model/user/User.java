package com.mbld.jigsly.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="puzzle_users")
public class User extends AbstractUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String password;
    private String email;
    private Date lastLoginDate;
    private Date joinDate;

    private String username;
    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;

}