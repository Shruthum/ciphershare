package com.ciphershare.v1.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ciphershare.v1.entity.FileMetaData;
import com.ciphershare.v1.repository.FileMetaDataRepository;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class FileService {

    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;
    @Autowired
    private MinioClient minioClient;

    private final Path storageDirectory = Paths.get("storage/");

    public FileMetaData storeFile(MultipartFile file,String username,String bucketName) throws Exception {

        byte[] encryptedData = encryptionService.encrypt(file.getBytes());
        Path filePath = storageDirectory.resolve(file.getOriginalFilename()+".cipher");
        Files.write(filePath,encryptedData);

        InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(encryptedData));

        String filename = file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                .bucket(bucketName)
                .object(filename)
                .stream(inputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
                );

        FileMetaData filemetaData = new FileMetaData();
        filemetaData.setFileName(filename);
        filemetaData.setFileSize(file.getSize());
        filemetaData.setFileType(file.getContentType());
        filemetaData.setUploadedBy(username);
        filemetaData.setStoragePath(filePath.toString());

        fileMetaDataRepository.save(filemetaData);
        return filemetaData;

    }

    public void shareFile(Long fileId,String username){
        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId).orElseThrow(
            () -> new RuntimeException("File not found")
        );
        fileMetaData.getSharedwithUsers().add(username);
        fileMetaDataRepository.save(fileMetaData);
    }
}
