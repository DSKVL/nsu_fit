package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.User;
import com.example.trainstationtemp.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
    Optional<UserAuthority> findUserAuthorityByUserAndAuthority(User user, String authority);
}