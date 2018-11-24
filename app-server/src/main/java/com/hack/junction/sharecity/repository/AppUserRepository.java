package com.hack.junction.sharecity.repository;

import com.hack.junction.sharecity.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByName(String name);

    Optional<AppUser> findByNameOrEmail(String name, String email);

    Boolean existsByName(String name);
}
