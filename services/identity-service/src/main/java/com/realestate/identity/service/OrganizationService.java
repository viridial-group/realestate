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
}
