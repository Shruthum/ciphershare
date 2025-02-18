package com.ciphershare.v1.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinIOConfig {

    @Autowired
    private MinIOConfigProperties minIOConfigProperties;
    @Bean
    public MinioClient minIOClient(){
        return MinioClient.builder().endpoint(minIOConfigProperties.getMiniourl()).credentials(minIOConfigProperties.getAccessKey(),minIOConfigProperties.getSecretKey()).build();
    }
}
