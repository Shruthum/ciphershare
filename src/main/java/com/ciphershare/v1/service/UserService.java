package com.ciphershare.v1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ciphershare.v1.component.JwtProvider;
import com.ciphershare.v1.entity.User;
import com.ciphershare.v1.entity.User.Role;
import com.ciphershare.v1.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userrepository;
    @Autowired
    private BCryptPasswordEncoder passwordencoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtProvider jwtProvider;

    public User register(String username,String name,String password,String role,String email){

        Optional<User> existing_user = userrepository.findByusername(username);
        if(existing_user.isPresent()){
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setRole(Role.valueOf(role));
        user.setPassword(passwordencoder.encode(password));
        user.setEmail(email);

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

    public String loginservice(String username,String password){

        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return jwtProvider.generateToken(userDetails.getUsername());
    }

}
