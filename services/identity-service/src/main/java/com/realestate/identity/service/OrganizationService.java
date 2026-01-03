package com.realestate.identity.service;

import com.realestate.identity.entity.Organization;
import com.realestate.identity.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Transactional(readOnly = true)
    public Optional<Organization> getOrganizationById(Long id) {
        return organizationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsByUserId(Long userId) {
        return organizationRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Organization> getRootOrganizations() {
        return organizationRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Organization> getChildren(Long parentId) {
        return organizationRepository.findByParentId(parentId);
    }

    @Transactional
    public Organization updateOrganization(Long id, Organization organizationDetails) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));

        if (organizationDetails.getName() != null) {
            organization.setName(organizationDetails.getName());
        }
        if (organizationDetails.getDescription() != null) {
            organization.setDescription(organizationDetails.getDescription());
        }
        if (organizationDetails.getDomain() != null) {
            organization.setDomain(organizationDetails.getDomain());
        }
        if (organizationDetails.getActive() != null) {
            organization.setActive(organizationDetails.getActive());
        }
        if (organizationDetails.getParent() != null) {
            organization.setParent(organizationDetails.getParent());
        }

        return organizationRepository.save(organization);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found with id: " + id);
        }
        organizationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Organization> getOrganizationByName(String name) {
        return organizationRepository.findByName(name);
    }

    @Transactional
    public Organization createOrUpdateOrganization(Organization organization) {
        return organizationRepository.findByName(organization.getName())
                .map(existing -> {
                    if (organization.getDescription() != null) {
                        existing.setDescription(organization.getDescription());
                    }
                    if (organization.getDomain() != null) {
                        existing.setDomain(organization.getDomain());
                    }
                    if (organization.getActive() != null) {
                        existing.setActive(organization.getActive());
                    }
                    return organizationRepository.save(existing);
                })
                .orElseGet(() -> organizationRepository.save(organization));
    }

    @Transactional
    public Organization updateOrganizationSettings(Long id, com.realestate.identity.dto.OrganizationSettingsDTO settingsDTO) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));

        if (settingsDTO.getLogoUrl() != null) {
            organization.setLogoUrl(settingsDTO.getLogoUrl());
        }
        if (settingsDTO.getAddress() != null) {
            organization.setAddress(settingsDTO.getAddress());
        }
        if (settingsDTO.getCity() != null) {
            organization.setCity(settingsDTO.getCity());
        }
        if (settingsDTO.getPostalCode() != null) {
            organization.setPostalCode(settingsDTO.getPostalCode());
        }
        if (settingsDTO.getCountry() != null) {
            organization.setCountry(settingsDTO.getCountry());
        }
        if (settingsDTO.getPhone() != null) {
            organization.setPhone(settingsDTO.getPhone());
        }
        if (settingsDTO.getEmail() != null) {
            organization.setEmail(settingsDTO.getEmail());
        }
        if (settingsDTO.getCustomDomains() != null) {
            organization.setCustomDomains(settingsDTO.getCustomDomains());
        }
        if (settingsDTO.getQuotas() != null) {
            organization.setQuotas(settingsDTO.getQuotas());
        }

        return organizationRepository.save(organization);
    }

    @Transactional(readOnly = true)
    public com.realestate.identity.dto.OrganizationSettingsDTO getOrganizationSettings(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));

        com.realestate.identity.dto.OrganizationSettingsDTO settingsDTO = new com.realestate.identity.dto.OrganizationSettingsDTO();
        settingsDTO.setLogoUrl(organization.getLogoUrl());
        settingsDTO.setAddress(organization.getAddress());
        settingsDTO.setCity(organization.getCity());
        settingsDTO.setPostalCode(organization.getPostalCode());
        settingsDTO.setCountry(organization.getCountry());
        settingsDTO.setPhone(organization.getPhone());
        settingsDTO.setEmail(organization.getEmail());
        settingsDTO.setCustomDomains(organization.getCustomDomains());
        settingsDTO.setQuotas(organization.getQuotas());

        return settingsDTO;
    }
}
