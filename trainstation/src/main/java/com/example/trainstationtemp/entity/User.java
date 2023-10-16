package com.example.trainstationtemp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@Entity(name="users")
public class User  {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;

    @OneToMany(cascade = CascadeType.ALL)
    List<UserAuthority> authorities = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        enabled = true;
    }

    public User() {}

    public void addAuthority(String authority) {
        authorities.add(new UserAuthority(this, authority));
    }
}