package com.ciphershare.v1.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ciphershare.v1.entity.FileMetaData;
import com.ciphershare.v1.entity.FileVersion;
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

        putInMinIO(bucketName, filename, inputStream, filename, file.getSize());

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

    public FileMetaData storeNewVersion(Long fileId,MultipartFile file,String bucketName) throws Exception {

        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId).orElseThrow(
            () -> new RuntimeException("File not found!")
        );

        int newVersion = fileMetaData.getVersions().size() + 1;

        String newFileName = fileMetaData.getFileName()+"__v"+newVersion;

        byte[] encryptedData = encryptionService.encrypt(file.getBytes( ));
        Path filePath = storageDirectory.resolve(newFileName+".cipher");
        Files.write(filePath,encryptedData);

        InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(encryptedData));

        putInMinIO(bucketName,newFileName,inputStream,file.getContentType(),file.getSize());

        FileVersion version = new FileVersion();

        version.setFileMetaData(fileMetaData);
        version.setVersionNumber(newVersion);
        version.setStoragePath(filePath.toString());
        version.setCreatedAt(LocalDateTime.now());

        fileMetaData.addNewVersionOfFile(version);
        fileMetaDataRepository.save(fileMetaData);

        return fileMetaData;
    }

    public FileMetaData rollbackFileVersion(Long fileId,int version){

        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId).orElseThrow(
            () -> new RuntimeException("File not found!")
        );

        FileVersion versionToRestore = fileMetaData.getVersions()
                                                .stream()
                                                .filter(fileversion -> fileversion.getVersionNumber() == version)
                                                .findFirst()
                                                .orElseThrow(() -> new RuntimeException("Version for the file not found!"));

         fileMetaData.setFileName(versionToRestore.getStoragePath());
         fileMetaDataRepository.save(fileMetaData);

         return fileMetaData;
    }

    private void putInMinIO(String bucketName,String fileName,InputStream inputStream,String fileType,long fileSize){
       try{
           minioClient.putObject(
                       PutObjectArgs.builder()
                       .bucket(bucketName)
                       .object(fileName)
                       .stream(inputStream,fileSize, -1)
                       .contentType(fileType)
                       .build()
                       );
        }
        catch(Exception e){
            throw new RuntimeException("Cannot store the file");
        }
    }
}
