package com.ciphershare.v1.service;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ciphershare.v1.entity.Logging;
import com.ciphershare.v1.entity.Mapping;
import com.ciphershare.v1.repository.LoggingRepository;


@Service
public class LoggingService {

    @Autowired
    private LoggingRepository loggingRepository;

    public void logaction(String username,String action,String details){

        StringBuffer str = new StringBuffer(action+" "+details);

        Mapping map = new Mapping();
        map.setJoint(str.toString());
        Logging logs = new Logging();
        logs.setUsername(username);
        logs.getDetails().add(map);
        loggingRepository.save(logs);
    }

    public Optional<Logging> findByUsername(String username){
        return loggingRepository.findByUsername(username);
    }
}
