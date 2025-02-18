package com.drivochat.v1.repository;

import com.drivochat.v1.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByusername(String username);
    Optional<List<User>> findByname(String name);
    //suppose don't know about the username we will try searching by the firstname from name of the user
}
