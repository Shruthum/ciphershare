package com.ciphershare.v1.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ciphershare.v1.entity.Logging;
import com.ciphershare.v1.service.LoggingService;

@Controller
@RequestMapping("/log")
public class LoggingController {

    @Autowired
    private LoggingService loggingService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getLogs(@PathVariable String username){

        Optional<Logging> logs = loggingService.findByUsername(username);
        if(logs.isPresent()){
            Logging extracted_log = logs.get();
            return ResponseEntity.ok(extracted_log);
        }
        return ResponseEntity.internalServerError().body("No logs");
    }
}
