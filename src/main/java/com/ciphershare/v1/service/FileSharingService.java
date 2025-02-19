package com.ciphershare.v1.service;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ciphershare.v1.entity.FileMetaData;
import com.ciphershare.v1.entity.FileMetaData.Access;
import com.ciphershare.v1.repository.FileMetaDataRepository;

@Service
public class FileSharingService {

    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;

    @Autowired
    private EnvService envService;

    public String generatePublicLink(Long fileMetadataId){

        Optional<FileMetaData> file = fileMetaDataRepository.findById(fileMetadataId);
        if(file.isPresent()){
            FileMetaData fileMetaData = file.get();
            fileMetaData.setAccess(Access.PUBLIC);
            fileMetaDataRepository.save(fileMetaData);

            return ""+envService.getInstanceIP()+":8080/files/download/"+fileMetaData.getFileName();
        }
        throw new RuntimeException("File not found");
    }

    public void setFileAccess(Long fileMetadataId,Access access){

        Optional<FileMetaData> file = fileMetaDataRepository.findById(fileMetadataId);
        if(file.isPresent()){
            FileMetaData fileMetaData = file.get();
            fileMetaData.setAccess(access);
            fileMetaDataRepository.save(fileMetaData);
        }else{
            throw new RuntimeException("File not found");
        }
    }

    public void revokePublicAccess(Long fileMetadataId){

        Optional<FileMetaData> file = fileMetaDataRepository.findById(fileMetadataId);
        if(file.isPresent()){
            FileMetaData fileMetaData = file.get();
            fileMetaData.setAccess(Access.PRIVATE);
            fileMetaDataRepository.save(fileMetaData);
        }else{
            throw new RuntimeException("File access revoked");
        }
    }
}
