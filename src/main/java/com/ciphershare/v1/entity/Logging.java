package com.ciphershare.v1.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "joint",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Mapping> details;

    private LocalDateTime localDateTime;

    public Logging(String username,List<Mapping> details){
        this.username = username;
        this.details = details;
        this.localDateTime = LocalDateTime.now();
    }

}
