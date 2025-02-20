package com.ciphershare.v1.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "logs")
@NoArgsConstructor
@Getter
@Setter
public class Logging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loggingId;

    private String username;
    private List<String> action;
    private String details;
    private LocalDateTime localDateTime;

    public Logging(String username,List<String> action,String details){
        this.username = username;
        this.action = action;
        this.details = details;
        this.localDateTime = LocalDateTime.now();
    }

}
