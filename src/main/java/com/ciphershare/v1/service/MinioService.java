package com.ciphershare.v1.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ciphershare.v1.configuration.MinIOConfigProperties;
import com.ciphershare.v1.entity.FileMetaData;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileSearchService fileSearchService;
    @Autowired
    private FileService filePathStorageService;


    private MinIOConfigProperties minIOConfigProperties;
    private final String bucketName = minIOConfigProperties.getBucketName();

    public void uploadFile(MultipartFile file,String username){
        try{

            FileMetaData filemetaData = filePathStorageService.storeFile(file,username,bucketName);
            fileSearchService.indexFileMetaData(filemetaData);

        }catch(Exception e){
            throw new RuntimeException("Error Uploading file: "+e.getMessage());
        }
    }

    public void uploadNewVersionFile(String fileId,MultipartFile file){

        try{

            FileMetaData fileMetaData = filePathStorageService.storeNewVersion(Long.parseLong(fileId), file, bucketName);
            fileSearchService.indexFileMetaData(fileMetaData);

        }catch(Exception e){
            throw new RuntimeException("Error uploading new version file: "+e.getMessage());
        }
    }

    public InputStream downloadFile(String fileName){
        // This function will return encrypt data
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
