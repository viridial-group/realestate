package com.realestate.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'inscription avec abonnement
 * Contient les informations minimales nécessaires pour créer un utilisateur, une organisation et un abonnement
 */
public class SubscribeRequest {

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est requis")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String lastName;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 8, max = 255, message = "Le mot de passe doit contenir entre 8 et 255 caractères")
    @jakarta.validation.constraints.Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre"
    )
    private String password;

    @NotBlank(message = "Le nom de l'organisation est requis")
    @Size(max = 255, message = "Le nom de l'organisation ne peut pas dépasser 255 caractères")
    private String organizationName;

    @NotNull(message = "L'ID du plan est requis")
    private Long planId;

    // Optionnel
    private String phone;

    public SubscribeRequest() {
    }

    public SubscribeRequest(String firstName, String lastName, String email, String password, String organizationName, Long planId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.organizationName = organizationName;
        this.planId = planId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

