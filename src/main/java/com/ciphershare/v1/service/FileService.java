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
import com.ciphershare.v1.entity.User;
import com.ciphershare.v1.repository.FileMetaDataRepository;
import com.ciphershare.v1.repository.UserRepository;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class FileService {

    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FileCacheService fileCacheService;
    @Autowired
    private NotificationHandler notificationHandler;

    private final Path storageDirectory = Paths.get("storage/");

    private User returnUser(String username) throws Exception{
        return userRepository.findByusername(username).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public FileMetaData storeFile(MultipartFile file,String username,String bucketName) throws Exception {

        User user = returnUser(username);
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

        loggingService.logaction(username,"UPLOADED","Uploaded File: "+filename);
        emailService.sendEmail(user.getEmail(), "File Uploaded","Your file "+filename+" has been uploaded");
        fileCacheService.storeFileinCache(filename, encryptedData);
        notificationHandler.broadcast("File Uploaded: "+filename);
        return filemetaData;

    }

    public void shareFile(Long fileId,String username) throws Exception {

        User user = returnUser(username);

        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId).orElseThrow(
            () -> new RuntimeException("File not found")
        );
        fileMetaData.getSharedwithUsers().add(username);
        fileMetaDataRepository.save(fileMetaData);

        String owner = fileMetaData.getUploadedBy();

        loggingService.logaction(owner,"SHARED","Shared with: "+username);
        emailService.sendEmail(user.getEmail(),"File Shared with","File has been shared with "+username);
        notificationHandler.broadcast("File shared with "+username);
    }

    public FileMetaData storeNewVersion(Long fileId,MultipartFile file,String bucketName) throws Exception {

        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId).orElseThrow(
            () -> new RuntimeException("File not found!")
        );

        User user = returnUser(fileMetaData.getUploadedBy());
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

        loggingService.logaction(fileMetaData.getUploadedBy(),"UPLOADED","Uploaded New Version of File: "+file.getOriginalFilename());
        emailService.sendEmail(user.getEmail(), "File New Version Uploaded","Your file "+file.getOriginalFilename()+" has been uploaded");
        notificationHandler.broadcast("New Version File is uploaded");
        fileCacheService.storeFileinCache(newFileName, encryptedData);
        fileMetaDataRepository.save(fileMetaData);

        return fileMetaData;
    }

    public FileMetaData rollbackFileVersion(Long fileId,int version) throws Exception{

        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId).orElseThrow(
            () -> new RuntimeException("File not found!")
        );

        User user = returnUser(fileMetaData.getUploadedBy());
        FileVersion versionToRestore = fileMetaData.getVersions()
                                                .stream()
                                                .filter(fileversion -> fileversion.getVersionNumber() == version)
                                                .findFirst()
                                                .orElseThrow(() -> new RuntimeException("Version for the file not found!"));

         fileMetaData.setFileName(versionToRestore.getStoragePath());
         fileMetaDataRepository.save(fileMetaData);

         loggingService.logaction(fileMetaData.getUploadedBy(), "ROLLBACK","Rollback to specified version: "+version);

         emailService.sendEmail(user.getEmail(), "File Rollbacked","Your file has been rollbacked!");

         notificationHandler.broadcast("File has been rollbacked!");
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
