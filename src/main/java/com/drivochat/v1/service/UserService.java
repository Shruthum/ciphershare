package com.drivochat.v1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.drivochat.v1.entity.User;
import com.drivochat.v1.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userrepository;
    @Autowired
    private BCryptPasswordEncoder passwordencoder;

    public User register(String username,String name,String password,String role){
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setRole(role);
        user.setPassword(passwordencoder.encode(password));

        //saving the user into the repository
        return userrepository.save(user);
    }

    public Optional<User> findByUsername(String username){
        return userrepository.findByusername(username);
    }
    public Optional<List<User>> findByname(String name){
        String[] name_array = name.split(" ");
        return userrepository.findByname(name_array[0]);
    }

}
