package com.ciphershare.v1.controller;


import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ciphershare.v1.entity.FileMetaData;
import com.ciphershare.v1.entity.FileMetaData.Access;
import com.ciphershare.v1.repository.FileMetaDataRepository;
import com.ciphershare.v1.service.EncryptionService;
import com.ciphershare.v1.service.FileCacheService;
import com.ciphershare.v1.service.FileSearchService;
import com.ciphershare.v1.service.FileSharingService;
import com.ciphershare.v1.service.LoggingService;
import com.ciphershare.v1.service.MinioService;


@Controller
@RequestMapping("/files")
public class FilesController {

    @Autowired
    private MinioService minioService;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;
    @Autowired
    private FileSearchService fileSearchService;
    @Autowired
    private FileSharingService fileSharingService;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private FileCacheService fileCacheService;
    @Autowired
    private LoggingService loggingService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("username") String username){

        minioService.uploadFile(file,username);
        return ResponseEntity.ok("File Uploaded Successfully!");
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("username") String username){

        minioService.multipleUploadFile(files, username);
        return ResponseEntity.ok("Files Uploaded successfully!");
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName,Principal principal){

        byte[] encrypted = fileCacheService.getCachedFile(fileName);
        byte[] decrypted_data = new byte[0];
        if(encrypted == null){
            try {

                InputStream inputStream = minioService.downloadFile(fileName);
                encrypted = inputStream.readAllBytes();
                if(encrypted == null){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                decrypted_data = encryptionService.decrypt(encrypted);

                loggingService.logaction(principal.getName(),"DOWNLOAD","Downloaded file: "+fileName);


            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }

        }
            return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,"attachment: filename="+fileName)
                        .body(decrypted_data);
    }

    @PostMapping("/version/{filemetaDataId}")
    public ResponseEntity<String> uploadNewVersion(@PathVariable String filemetaDataId,@RequestParam("file") MultipartFile file){

        try {
            minioService.uploadNewVersionFile(filemetaDataId, file);
            return ResponseEntity.ok("New Version of File Uploaded Successfully!");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/rollback/{filemetaDataId}")
    public ResponseEntity<String> rollbackFile(@PathVariable String filemetaDataId,@RequestParam String version){

        try{
            minioService.rollbackFile(filemetaDataId, Integer.parseInt(version));
            return ResponseEntity.ok("File restored to specified version");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("Cannot rollback");
        }

    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName){
        minioService.deleteFile(fileName);
        fileCacheService.removeFileFromCache(fileName);
        return ResponseEntity.ok("File Deleted: "+fileName);
    }

    @GetMapping("/metadata")
    public List<FileMetaData> getAllFiles(){
        return fileMetaDataRepository.findAll();
    }

    @GetMapping("/metadata/{username}")
    public List<FileMetaData> getFilesByUser(@PathVariable String username){
        return fileMetaDataRepository.findByUploadedBy(username);
    }

    @GetMapping("/search")
    public List<FileMetaData> searchFiles(@RequestParam String keyword,@RequestParam String page,@RequestParam String size) {
        if(page != null && size != null){
            return fileSearchService.searchFiles(keyword, Integer.parseInt(page),Integer.parseInt(size));
        }
        return fileSearchService.searchFiles(keyword);
    }

    @PostMapping("/share/{fileId}")
    public ResponseEntity<String> generatePublicLink(@PathVariable Long fileId,@RequestParam int expiry){

        String generated_link = fileSharingService.generatePublicLink(fileId,expiry);
        return ResponseEntity.ok("Public Link generated: "+generated_link);
    }

    @PostMapping("/access/{fileId}")
    public ResponseEntity<String> setFileAcess(@PathVariable Long fileId,@RequestParam String access){

        if(access.equals("private")){
            fileSharingService.revokePublicAccess(fileId);
            return ResponseEntity.ok("File access revoked");
        }
        fileSharingService.setFileAccess(fileId, Access.valueOf(access));
        return ResponseEntity.ok("File Access updated to: "+access);
    }
}
