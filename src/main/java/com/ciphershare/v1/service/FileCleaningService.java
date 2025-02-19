package com.ciphershare.v1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ciphershare.v1.entity.FileMetaData;
import com.ciphershare.v1.repository.FileMetaDataRepository;


@Service
public class FileCleaningService {

    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;

    @Scheduled(fixedRate = 120000)
    public void deleteExpiredFiles() {
        List<FileMetaData> expiredFiles = fileMetaDataRepository.findAll().stream().filter(FileMetaData::isExpired).collect(Collectors.toList());

        for(FileMetaData file : expiredFiles){
            fileMetaDataRepository.delete(file);
        }
    }
}
