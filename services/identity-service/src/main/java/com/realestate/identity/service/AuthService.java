package com.realestate.identity.service;

import com.realestate.common.client.EmailServiceClient;
import com.realestate.identity.dto.AuthResponse;
import com.realestate.identity.dto.LoginRequest;
import com.realestate.identity.dto.RefreshTokenRequest;
import com.realestate.identity.dto.RegisterRequest;
import com.realestate.identity.entity.User;
import com.realestate.identity.entity.Role;
import com.realestate.identity.repository.UserRepository;
import com.realestate.identity.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Autowired(required = false)
    private EmailServiceClient emailServiceClient;
    
    @Value("${app.frontend.url:http://localhost:3003}")
    private String frontendUrl;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        // Assigner le rôle INDIVIDUAL pour les particuliers
        Role individualRole = roleRepository.findByName("INDIVIDUAL")
                .orElseThrow(() -> new IllegalStateException(
                        "INDIVIDUAL role not found. Please ensure roles are initialized in the database."));
        
        Set<Role> roles = new HashSet<>();
        roles.add(individualRole);
        user.setRoles(roles);

        user = userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Envoyer l'email de bienvenue de manière asynchrone
        sendWelcomeEmail(user);

        return new AuthResponse(accessToken, refreshToken, jwtService.getExpiration());
    }
    
    /**
     * Envoie un email de bienvenue à l'utilisateur
     */
    private void sendWelcomeEmail(User user) {
        if (emailServiceClient == null) {
            return; // Service emailing non disponible
        }
        
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", user.getFirstName() != null ? user.getFirstName() : "Utilisateur");
            variables.put("appUrl", frontendUrl);
            
            // Récupérer l'organizationId depuis OrganizationUser (peut être null pour les particuliers)
            Long organizationId = null;
            // Note: Pour les particuliers, organizationId sera null, ce qui est correct
            
            // Envoyer de manière asynchrone (fire and forget)
            emailServiceClient.sendEmailFromTemplateAsync(
                    "welcome_email",
                    user.getEmail(),
                    user.getId(),
                    organizationId, // null pour les particuliers
                    variables,
                    null // Pas besoin de token pour l'envoi d'email
            );
        } catch (Exception e) {
            // Ne pas faire échouer l'inscription si l'email échoue
            // Log l'erreur mais continue
        }
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        return new AuthResponse(accessToken, refreshToken, jwtService.getExpiration());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtService.extractUsername(refreshToken);

        if (!jwtService.validateToken(refreshToken, email)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        String newAccessToken = jwtService.generateToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        return new AuthResponse(newAccessToken, newRefreshToken, jwtService.getExpiration());
    }

    @Transactional
    public void logout(String token) {
        // In a real implementation, you might want to blacklist the token in Redis
        // For now, we'll just validate that the token exists
        String email = jwtService.extractUsername(token);
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

