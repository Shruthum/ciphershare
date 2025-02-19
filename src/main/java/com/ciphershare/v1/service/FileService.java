package com.ciphershare.v1.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ciphershare.v1.entity.FileMetaData;
import com.ciphershare.v1.repository.FileMetaDataRepository;

@Service
public class FileService {

    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;

    private final Path storageDirectory = Paths.get("storage/");

    public String storeFile(MultipartFile file) throws Exception {

        byte[] encryptedData = encryptionService.encrypt(file.getBytes());
        Path filePath = storageDirectory.resolve(file.getOriginalFilename()+".cipher");
        Files.write(filePath,encryptedData);
        return filePath.toString();

    }

    public void shareFile(Long fileId,String username){
        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId).orElseThrow(
            () -> new RuntimeException("File not found")
        );
        fileMetaData.getSharedwithUsers().add(username);
        fileMetaDataRepository.save(fileMetaData);
    }
}
