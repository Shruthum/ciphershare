package com.ciphershare.v1.service;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ciphershare.v1.configuration.MinIOConfigProperties;
import com.ciphershare.v1.entity.FileMetaData;
import com.ciphershare.v1.repository.FileMetaDataRepository;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;
    @Autowired
    private FileSearchService fileSearchService;

    private MinIOConfigProperties minIOConfigProperties;
    private final String bucketName = minIOConfigProperties.getBucketName();

    public String uploadFile(MultipartFile file,String username){
        try{
            String filename = UUID.randomUUID()+"_"+ file.getOriginalFilename();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            FileMetaData filemetaData = new FileMetaData();
            filemetaData.setFileName(filename);
            filemetaData.setFileSize(file.getSize());
            filemetaData.setFileType(file.getContentType());
            filemetaData.setUploadedBy(username);
            filemetaData.setStoragePath(filename);

            fileMetaDataRepository.save(filemetaData);
            fileSearchService.indexFileMetaData(filemetaData);

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
