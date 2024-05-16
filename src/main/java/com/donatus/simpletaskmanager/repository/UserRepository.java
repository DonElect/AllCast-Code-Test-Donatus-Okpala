package com.donatus.simpletaskmanager.repository;

import com.donatus.simpletaskmanager.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    Optional<UserEntity> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseOrEmail(String firstName, String lastName, String email);
}