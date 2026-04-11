package com.homeflex.core.service;

import com.homeflex.core.domain.entity.Agency;
import com.homeflex.core.domain.repository.AgencyRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;

    @Transactional(readOnly = true)
    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Agency getAgencyById(UUID id) {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
    }

    @Transactional
    public Agency createAgency(Agency agency) {
        return agencyRepository.save(agency);
    }

    @Transactional
    public Agency verifyAgency(UUID id) {
        Agency agency = getAgencyById(id);
        agency.setIsVerified(true);
        return agencyRepository.save(agency);
    }
}
