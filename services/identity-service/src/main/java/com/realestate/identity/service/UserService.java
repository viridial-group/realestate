package com.realestate.identity.service;

import com.realestate.identity.entity.Organization;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.OrganizationRepository;
import com.realestate.identity.repository.UserRepository;
import com.realestate.identity.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public UserService(UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(String email) {
        return userRepository.findByEmailWithOrganization(email)
                .orElseGet(() -> userRepository.findByEmailWithRolesAndPermissions(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findByIdWithRolesAndOrganization(id)
                .or(() -> userRepository.findByIdWithRoles(id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Récupérer les utilisateurs avec filtres (organizationId, role, status, search)
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersWithFilters(
            Long organizationId,
            String role,
            String status,
            String search,
            Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        // Filtre par organisation
        if (organizationId != null) {
            spec = spec.and(UserSpecification.hasOrganization(organizationId));
        }

        // Filtre par rôle
        if (role != null && !role.isEmpty() && !"all".equalsIgnoreCase(role)) {
            spec = spec.and(UserSpecification.hasRole(role));
        }

        // Filtre par statut
        if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
            spec = spec.and(UserSpecification.hasStatus(status));
        }

        // Recherche textuelle
        if (search != null && !search.isEmpty()) {
            spec = spec.and(UserSpecification.searchByText(search));
        }

        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEnabled() != null) {
            user.setEnabled(userDetails.getEnabled());
        }
        // Note: L'organisation est maintenant gérée via OrganizationUser

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}

