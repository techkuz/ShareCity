package com.hack.junction.sharecity.repository;

import com.hack.junction.sharecity.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByName(String name);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByNameOrEmail(String name, String email);

    Optional<AppUser> findByUsernameOrEmail(String username, String email);

    List<AppUser> findByIdIn(List<Long> userIds);

    Boolean existsByName(String name);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);


}
