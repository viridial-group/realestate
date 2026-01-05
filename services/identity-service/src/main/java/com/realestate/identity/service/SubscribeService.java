package com.realestate.identity.service;

import com.realestate.common.client.EmailServiceClient;
import com.realestate.common.exception.BadRequestException;
import com.realestate.common.exception.ResourceNotFoundException;
import com.realestate.identity.dto.AuthResponse;
import com.realestate.identity.dto.SubscribeRequest;
import com.realestate.identity.dto.SubscribeResponse;
import com.realestate.identity.entity.Organization;
import com.realestate.identity.entity.User;
import com.realestate.identity.repository.OrganizationRepository;
import com.realestate.identity.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service pour l'inscription avec abonnement
 * Crée l'utilisateur, l'organisation, assigne l'utilisateur et crée l'abonnement en une seule transaction
 */
@Service
public class SubscribeService {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeService.class);

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OrganizationUserService organizationUserService;
    private final EntityManager entityManager;
    
    @Autowired(required = false)
    private EmailServiceClient emailServiceClient;
    
    @Value("${app.frontend.url:http://localhost:3003}")
    private String frontendUrl;

    public SubscribeService(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            OrganizationUserService organizationUserService,
            EntityManager entityManager) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.organizationUserService = organizationUserService;
        this.entityManager = entityManager;
    }

    // Patterns de validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    // Pattern pour validation stricte du mot de passe (optionnel, peut être activé si nécessaire)
    // private static final Pattern PASSWORD_PATTERN = Pattern.compile(
    //         "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    // );
    private static final Pattern ORGANIZATION_NAME_PATTERN = Pattern.compile(
            "^[A-Za-z0-9À-ÿ\\s\\-_.]{2,255}$"
    );

    /**
     * Inscription complète avec création d'organisation et abonnement
     * 
     * @param request Les informations d'inscription
     * @return La réponse avec les tokens d'authentification et les informations de l'organisation/abonnement
     * @throws BadRequestException si les données sont invalides
     * @throws ResourceNotFoundException si le plan n'existe pas
     */
    @Transactional
    public SubscribeResponse subscribe(SubscribeRequest request) {
        logger.info("Starting subscription process for email: {}", request.getEmail());

        // Validation des données
        validateSubscribeRequest(request);

        // 1. Vérifier que l'email n'existe pas déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un compte avec cet email existe déjà. Veuillez utiliser un autre email ou vous connecter.");
        }

        // 2. Vérifier que le nom d'organisation n'existe pas déjà
        if (organizationRepository.findByName(request.getOrganizationName()).isPresent()) {
            throw new BadRequestException("Une organisation avec ce nom existe déjà. Veuillez choisir un autre nom.");
        }

        // 3. Vérifier que le plan existe et est actif
        validatePlan(request.getPlanId());

        // 4. Créer l'utilisateur
        User user;
        try {
            user = new User();
            user.setEmail(request.getEmail().trim().toLowerCase());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName().trim());
            user.setLastName(request.getLastName().trim());
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user = userRepository.save(user);
            logger.info("User created with ID: {}", user.getId());
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new BadRequestException("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }

        // 5. Créer l'organisation
        Organization organization;
        try {
            organization = new Organization();
            organization.setName(request.getOrganizationName().trim());
            organization.setActive(true);
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                organization.setPhone(request.getPhone().trim());
            }
            // S'assurer que les champs JSONB restent null (ne pas les initialiser)
            organization.setCustomDomains(null);
            organization.setQuotas(null);
            organization = organizationRepository.save(organization);
            logger.info("Organization created with ID: {}", organization.getId());
        } catch (Exception e) {
            logger.error("Error creating organization: {}", e.getMessage(), e);
            throw new BadRequestException("Erreur lors de la création de l'organisation: " + e.getMessage());
        }

        // 6. Assigner l'utilisateur à l'organisation (comme organisation principale)
        // Cela assignera automatiquement le rôle ORGANIZATION_ADMIN
        try {
            organizationUserService.addUserToOrganization(user.getId(), organization.getId(), true);
            logger.info("User {} assigned to organization {} as primary", user.getId(), organization.getId());
        } catch (Exception e) {
            logger.error("Error assigning user to organization: {}", e.getMessage(), e);
            throw new BadRequestException("Erreur lors de l'assignation de l'utilisateur à l'organisation: " + e.getMessage());
        }

        // 7. Créer l'abonnement directement via SQL
        Long subscriptionId;
        try {
            subscriptionId = createSubscriptionDirectly(request.getPlanId(), organization.getId());
            logger.info("Subscription created with ID: {}", subscriptionId);
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating subscription: {}", e.getMessage(), e);
            throw new BadRequestException("Erreur lors de la création de l'abonnement: " + e.getMessage());
        }

        // 8. Récupérer le nom du plan
        String planName = getPlanName(request.getPlanId());

        // 9. Générer les tokens JWT
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtService.generateToken(user.getEmail());
            refreshToken = jwtService.generateRefreshToken(user.getEmail());
        } catch (Exception e) {
            logger.error("Error generating JWT tokens: {}", e.getMessage(), e);
            throw new BadRequestException("Erreur lors de la génération des tokens d'authentification");
        }
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, jwtService.getExpiration());

        // 10. Mettre à jour la date de dernière connexion
        try {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception e) {
            logger.warn("Error updating last login date for user {}: {}", user.getId(), e.getMessage());
            // Ne pas faire échouer l'inscription pour cette erreur mineure
        }

        logger.info("Subscription process completed successfully for user: {}", user.getEmail());
        
        // Envoyer l'email de confirmation de manière asynchrone
        sendRegistrationConfirmationEmail(user, organization);

        return new SubscribeResponse(
                authResponse,
                organization.getId(),
                subscriptionId,
                organization.getName(),
                planName
        );
    }

    /**
     * Valider les données de la requête d'inscription
     */
    private void validateSubscribeRequest(SubscribeRequest request) {
        // Validation de l'email
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("L'email est requis");
        }
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BadRequestException("Le format de l'email est invalide");
        }

        // Validation du mot de passe
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Le mot de passe est requis");
        }
        if (request.getPassword().length() < 8) {
            throw new BadRequestException("Le mot de passe doit contenir au moins 8 caractères");
        }
        if (request.getPassword().length() > 255) {
            throw new BadRequestException("Le mot de passe ne peut pas dépasser 255 caractères");
        }
        // Optionnel : validation de la force du mot de passe
        // Décommenter si vous voulez forcer un mot de passe fort
        // if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
        //     throw new BadRequestException("Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial");
        // }

        // Validation du prénom
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("Le prénom est requis");
        }
        if (request.getFirstName().length() > 100) {
            throw new BadRequestException("Le prénom ne peut pas dépasser 100 caractères");
        }

        // Validation du nom
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Le nom est requis");
        }
        if (request.getLastName().length() > 100) {
            throw new BadRequestException("Le nom ne peut pas dépasser 100 caractères");
        }

        // Validation du nom d'organisation
        if (request.getOrganizationName() == null || request.getOrganizationName().trim().isEmpty()) {
            throw new BadRequestException("Le nom de l'organisation est requis");
        }
        if (request.getOrganizationName().length() < 2) {
            throw new BadRequestException("Le nom de l'organisation doit contenir au moins 2 caractères");
        }
        if (request.getOrganizationName().length() > 255) {
            throw new BadRequestException("Le nom de l'organisation ne peut pas dépasser 255 caractères");
        }
        if (!ORGANIZATION_NAME_PATTERN.matcher(request.getOrganizationName()).matches()) {
            throw new BadRequestException("Le nom de l'organisation contient des caractères invalides. Utilisez uniquement des lettres, chiffres, espaces, tirets, points et underscores");
        }

        // Validation du plan
        if (request.getPlanId() == null) {
            throw new BadRequestException("L'ID du plan est requis");
        }
        if (request.getPlanId() <= 0) {
            throw new BadRequestException("L'ID du plan doit être un nombre positif");
        }

        // Validation du téléphone (optionnel)
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String phone = request.getPhone().trim();
            if (phone.length() > 20) {
                throw new BadRequestException("Le numéro de téléphone ne peut pas dépasser 20 caractères");
            }
        }
    }

    /**
     * Valider que le plan existe et est actif
     */
    private void validatePlan(Long planId) {
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT id, name, active FROM plans WHERE id = :planId"
            );
            query.setParameter("planId", planId);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            if (results.isEmpty()) {
                throw new ResourceNotFoundException("Plan", planId);
            }
            
            Object[] plan = results.get(0);
            Boolean active = (Boolean) plan[2];
            if (active == null || !active) {
                throw new BadRequestException("Le plan sélectionné n'est pas actif. Veuillez choisir un autre plan.");
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error validating plan {}: {}", planId, e.getMessage(), e);
            throw new BadRequestException("Erreur lors de la validation du plan: " + e.getMessage());
        }
    }

    /**
     * Créer l'abonnement directement via SQL
     */
    private Long createSubscriptionDirectly(Long planId, Long organizationId) {
        try {
            // Vérifier qu'il n'y a pas déjà un abonnement actif pour cette organisation
            Query existingQuery = entityManager.createNativeQuery(
                    "SELECT id FROM subscriptions WHERE organization_id = :organizationId AND active = true"
            );
            existingQuery.setParameter("organizationId", organizationId);
            List<?> existing = existingQuery.getResultList();
            
            if (!existing.isEmpty()) {
                throw new BadRequestException("Cette organisation a déjà un abonnement actif");
            }

            // Récupérer les informations du plan pour calculer la date de fin
            Query planQuery = entityManager.createNativeQuery(
                    "SELECT billing_period FROM plans WHERE id = :planId AND active = true"
            );
            planQuery.setParameter("planId", planId);
            List<?> results = planQuery.getResultList();
            
            if (results.isEmpty()) {
                throw new ResourceNotFoundException("Plan", planId);
            }
        
        String billingPeriod = (String) results.get(0);
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = null;
        
        // Calculer la date de fin selon la période de facturation
        if ("MONTHLY".equals(billingPeriod)) {
            endDate = startDate.plusMonths(1);
        } else if ("YEARLY".equals(billingPeriod)) {
            endDate = startDate.plusYears(1);
        }
        
        // Insérer l'abonnement
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO subscriptions (plan_id, organization_id, status, start_date, end_date, auto_renew, active, created_at, updated_at) " +
                "VALUES (:planId, :organizationId, 'ACTIVE', :startDate, :endDate, true, true, :now, :now) " +
                "RETURNING id"
        );
        insertQuery.setParameter("planId", planId);
        insertQuery.setParameter("organizationId", organizationId);
        insertQuery.setParameter("startDate", startDate);
        insertQuery.setParameter("endDate", endDate);
        insertQuery.setParameter("now", LocalDateTime.now());
        
            Object result = insertQuery.getSingleResult();
            if (result instanceof Number) {
                return ((Number) result).longValue();
            } else {
                logger.error("Invalid result type from subscription insert: {}", result != null ? result.getClass() : "null");
                throw new BadRequestException("Erreur lors de la création de l'abonnement. Veuillez réessayer.");
            }
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating subscription for organization {} and plan {}: {}", 
                    organizationId, planId, e.getMessage(), e);
            throw new BadRequestException("Erreur lors de la création de l'abonnement: " + e.getMessage());
        }
    }
    
    /**
     * Envoie un email de confirmation d'inscription
     */
    private void sendRegistrationConfirmationEmail(User user, Organization organization) {
        if (emailServiceClient == null) {
            return; // Service emailing non disponible
        }
        
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("firstName", user.getFirstName() != null ? user.getFirstName() : "Utilisateur");
            variables.put("email", user.getEmail());
            variables.put("registrationDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
            variables.put("loginUrl", frontendUrl + "/login");
            
            // Envoyer de manière asynchrone (fire and forget)
            emailServiceClient.sendEmailFromTemplateAsync(
                    "registration_confirmation",
                    user.getEmail(),
                    user.getId(),
                    organization.getId(),
                    variables,
                    null // Pas besoin de token pour l'envoi d'email
            );
        } catch (Exception e) {
            // Ne pas faire échouer l'inscription si l'email échoue
            logger.warn("Failed to send registration confirmation email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Récupérer le nom du plan
     */
    private String getPlanName(Long planId) {
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT name FROM plans WHERE id = :planId"
            );
            query.setParameter("planId", planId);
            List<?> results = query.getResultList();
            
            if (!results.isEmpty() && results.get(0) != null) {
                return (String) results.get(0);
            }
        } catch (Exception e) {
            logger.warn("Could not fetch plan name for plan ID {}: {}", planId, e.getMessage());
        }
        return "Plan " + planId;
    }
}

