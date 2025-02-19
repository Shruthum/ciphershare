package com.ciphershare.v1.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilePathStorageService {

    @Autowired
    private EncryptionService encryptionService;
    private final Path storageDirectory = Paths.get("storage/");

    public String storeFile(MultipartFile file) throws Exception {

        byte[] encryptedData = encryptionService.encrypt(file.getBytes());
        Path filePath = storageDirectory.resolve(file.getOriginalFilename()+".cipher");
        Files.write(filePath,encryptedData);
        return filePath.toString();

    }
}
