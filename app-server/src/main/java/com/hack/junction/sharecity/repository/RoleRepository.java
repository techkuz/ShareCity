package com.hack.junction.sharecity.repository;

import com.hack.junction.sharecity.model.Role;
import com.hack.junction.sharecity.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}

