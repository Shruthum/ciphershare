package com.ciphershare.v1.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ciphershare.v1.entity.User;


public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByusername(String username);
    Optional<List<User>> findByname(String name);
    //suppose don't know about the username we will try searching by the firstname from name of the user
}
