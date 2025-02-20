package com.ciphershare.v1.controller;


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ciphershare.v1.entity.User;
import com.ciphershare.v1.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;




@Controller
@RequestMapping("/auth/v1")
public class UserController {

    @Autowired
    private UserService userservice;

    @Autowired
    private BCryptPasswordEncoder passwordencoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> request){
        String username = request.get("username");
        String name = request.get("name");
        String role = request.get("role");
        String password = request.get("password");
        String email = request.get("email");

        try {
            User reg_user = userservice.register(username,name,password,role,email);
            return ResponseEntity.ok(reg_user);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Error user can't register");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> request){

        String username = request.get("username");
        String password = request.get("password");

        User user = userservice.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordencoder.matches(password,user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        String generatedJwtToken = userservice.loginservice(username, password);
        return ResponseEntity.ok(Map.of("token",generatedJwtToken));
    }



}
