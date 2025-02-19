package com.ciphershare.v1.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "file_meta_data")
@AllArgsConstructor
@Getter
@Setter
public class FileMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long filemetaDataId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String storagePath;
    private String uploadedBy;
    private LocalDateTime uploadTime;
    private LocalDateTime expireTime;
    @Enumerated(EnumType.STRING)
    private Access access;

    public enum Access{
        PRIVATE,PUBLIC
    }

    public FileMetaData(){
        this.uploadTime = LocalDateTime.now();
        this.access = Access.PRIVATE;
    }

    public boolean isExpired(){
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }
}
