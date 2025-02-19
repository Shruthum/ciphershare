package com.ciphershare.v1.service;

import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class EnvService {

    private final Dotenv dotenv;
    public EnvService(){
        this.dotenv = Dotenv.load();
    }
    public String getInstanceIP(){
        return dotenv.get("INSTANCE_IP");
    }
}
