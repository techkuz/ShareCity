package com.hack.junction.bytom.repository;

import com.hack.junction.bytom.model.Role;
import com.hack.junction.bytom.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
