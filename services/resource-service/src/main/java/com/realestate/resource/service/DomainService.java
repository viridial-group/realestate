package com.realestate.resource.service;

import com.realestate.resource.entity.Domain;
import com.realestate.resource.repository.DomainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DomainService {

    private final DomainRepository domainRepository;

    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @Transactional
    public Domain createDomain(Domain domain) {
        return domainRepository.save(domain);
    }

    @Transactional(readOnly = true)
    public Optional<Domain> getDomainById(Long id) {
        return domainRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Domain> getAllDomains() {
        return domainRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Domain> getActiveDomains() {
        return domainRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Optional<Domain> getDomainByName(String name) {
        return domainRepository.findByName(name);
    }

    @Transactional
    public Domain updateDomain(Long id, Domain domainDetails) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Domain not found with id: " + id));

        if (domainDetails.getName() != null) {
            domain.setName(domainDetails.getName());
        }
        if (domainDetails.getDescription() != null) {
            domain.setDescription(domainDetails.getDescription());
        }
        if (domainDetails.getActive() != null) {
            domain.setActive(domainDetails.getActive());
        }

        return domainRepository.save(domain);
    }

    @Transactional
    public void deleteDomain(Long id) {
        if (!domainRepository.existsById(id)) {
            throw new RuntimeException("Domain not found with id: " + id);
        }
        domainRepository.deleteById(id);
    }
}

