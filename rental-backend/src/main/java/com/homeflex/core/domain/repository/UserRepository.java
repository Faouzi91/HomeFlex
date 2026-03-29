package com.homeflex.core.domain.repository;


import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);

    // Additional methods for AdminService
    long countByRole(UserRole role);
}

