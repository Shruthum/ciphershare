package com.ciphershare.v1.service;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ciphershare.v1.configuration.MinIOConfigProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;
    private MinIOConfigProperties minIOConfigProperties;
    private final String bucketName = minIOConfigProperties.getBucketName();

    public String uploadFile(MultipartFile file){
        try{
            String filename = UUID.randomUUID()+"_"+ file.getOriginalFilename();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build()
            );
            return filename;
        }catch(Exception e){
            throw new RuntimeException("Error Uploading file: "+e.getMessage());
        }
    }

    public InputStream downloadFile(String fileName){
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while downloading file "+e.getMessage());
        }
    }
    public void deleteFile(String fileName){
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting file "+e.getMessage());
        }
    }

}
