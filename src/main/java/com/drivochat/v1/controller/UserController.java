package com.drivochat.v1.controller;


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.drivochat.v1.component.JwtProvider;
import com.drivochat.v1.entity.User;
import com.drivochat.v1.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/auth/v1")
public class UserController {

    @Autowired
    private UserService userservice;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> request){
        String username = request.get("username");
        String name = request.get("name");
        String role = request.get("role");
        String password = request.get("password");

        try {
            User reg_user = userservice.register(username,name,password,role);
            return ResponseEntity.ok(reg_user);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error user can't register");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> request){
        String username = request.get("username");
        String password = request.get("password");
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        final String generatedJwtToken = jwtProvider.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(Map.of("token",generatedJwtToken));
    }


}
