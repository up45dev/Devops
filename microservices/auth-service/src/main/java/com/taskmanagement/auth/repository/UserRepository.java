package com.taskmanagement.auth.repository;

import com.taskmanagement.auth.entity.User;
import com.taskmanagement.auth.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para entidade User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
}