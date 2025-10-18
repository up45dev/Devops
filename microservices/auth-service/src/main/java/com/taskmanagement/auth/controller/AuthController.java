package com.taskmanagement.auth.controller;

import com.taskmanagement.auth.dto.LoginRequest;
import com.taskmanagement.auth.dto.SignupRequest;
import com.taskmanagement.auth.dto.JwtResponse;
import com.taskmanagement.auth.dto.ApiResponse;
import com.taskmanagement.auth.entity.User;
import com.taskmanagement.auth.entity.Role;
import com.taskmanagement.auth.repository.UserRepository;
import com.taskmanagement.auth.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller para autenticação local
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    /**
     * Login com username/email e senha
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication.getName());
        
        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken, "Bearer"));
    }
    
    /**
     * Registro de novo usuário
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Username já está em uso!"));
        }
        
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Email já está em uso!"));
        }
        
        // Criar nova conta de usuário
        User user = new User(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword())
        );
        
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setRoles(Set.of(Role.USER));
        
        User result = userRepository.save(user);
        
        return ResponseEntity.ok(new ApiResponse(true, "Usuário registrado com sucesso!"));
    }
    
    /**
     * Refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        
        if (jwtService.validateToken(refreshToken) && jwtService.isRefreshToken(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            String newToken = jwtService.generateToken(username);
            String newRefreshToken = jwtService.generateRefreshToken(username);
            
            return ResponseEntity.ok(new JwtResponse(newToken, newRefreshToken, "Bearer"));
        }
        
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Token de refresh inválido!"));
    }
    
    /**
     * Validação de token
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        boolean isValid = jwtService.validateToken(token);
        
        if (isValid) {
            String username = jwtService.extractUsername(token);
            return ResponseEntity.ok(new ApiResponse(true, "Token válido para usuário: " + username));
        }
        
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Token inválido!"));
    }
    
    /**
     * Informações do usuário atual
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Usuário não autenticado!"));
        }
        
        User user = (User) authentication.getPrincipal();
        
        return ResponseEntity.ok(user);
    }
}