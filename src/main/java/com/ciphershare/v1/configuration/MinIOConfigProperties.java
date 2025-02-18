package com.ciphershare.v1.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;


@Configuration
@ConfigurationProperties(prefix = "minio.config")
@Getter
@Setter
public class MinIOConfigProperties {
    private String miniourl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
