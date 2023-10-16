package com.example.trainstationtemp.repository;

import com.example.trainstationtemp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsUserByUsername(String username);
    Optional<User> findByUsername(String username);
}
