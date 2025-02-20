package com.ciphershare.v1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ciphershare.v1.entity.Logging;

@Repository
public interface LoggingRepository extends JpaRepository<Logging,Long>{

    Optional<Logging> findByUsername(String username);
}
