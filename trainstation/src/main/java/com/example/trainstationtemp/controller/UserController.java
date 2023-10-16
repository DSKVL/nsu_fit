package com.example.trainstationtemp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.trainstationtemp.dto.SignUpDto;
import com.example.trainstationtemp.dto.UserDto;
import com.example.trainstationtemp.entity.User;
import com.example.trainstationtemp.entity.UserAuthority;
import com.example.trainstationtemp.repository.UserAuthorityRepository;
import com.example.trainstationtemp.repository.UserRepository;
import com.example.trainstationtemp.service.DtoMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = {"${app.security.cors.origin}"})
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired private UserRepository users;
    @Autowired private UserAuthorityRepository userAuthorityRepository;
    @Autowired private DtoMappings mapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    public UserController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public UserDetails getUser(Authentication authentication) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        Map<String, Object> attributes = token.getTokenAttributes();
        return userDetailsService.loadUserByUsername(attributes.get("username").toString());
    }

    @PostMapping("/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        // add check for username exists in a DB
        if (users.existsUserByUsername(signUpDto.username())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        var user = User.builder()
                .username(signUpDto.username())
                .password(passwordEncoder.encode(signUpDto.password()))
                .enabled(true)
                .authorities(new ArrayList<>())
                .build();

        user.addAuthority("READ_ROUTE");
        users.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    //TODO
    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto putUser(@RequestBody UserDto dto) {
        try {
            var user = users.findByUsername(dto.username()).orElseThrow();
            user.setAuthorities(dto.authorities().stream()
                    .map(auth -> userAuthorityRepository
                            .findUserAuthorityByUserAndAuthority(user, auth)
                            .orElse(new UserAuthority(user, auth)))
                    .toList());

            var saved = users.save(user);
            return mapper.mapToDto(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not Found", e);
        }
    }

}