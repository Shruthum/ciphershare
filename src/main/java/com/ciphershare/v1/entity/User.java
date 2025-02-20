package com.ciphershare.v1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username; //username for the backend and for the services
    private String name; //Name of the user
    private String password; //password has to be hashed before being stored in the database
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    public enum Role {
        ADMIN,USER
    }
    //TEMP,USER,ADMIN
    // TEMP will special user who want to share the data with limit of like 50MB
}
