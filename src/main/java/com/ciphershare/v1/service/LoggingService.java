package com.ciphershare.v1.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ciphershare.v1.entity.Logging;
import com.ciphershare.v1.repository.LoggingRepository;


@Service
public class LoggingService {

    @Autowired
    private LoggingRepository loggingRepository;

    public void logaction(String username,String action,String details){

        Logging logs = new Logging();
        logs.setUsername(username);
        logs.setDetails(details);
        logs.getAction().add(action);
        loggingRepository.save(logs);
    }

    public Optional<Logging> findByUsername(String username){
        return loggingRepository.findByUsername(username);
    }
}
