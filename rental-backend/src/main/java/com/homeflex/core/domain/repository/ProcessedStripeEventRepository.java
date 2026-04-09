package com.homeflex.core.domain.repository;

import com.homeflex.core.domain.entity.ProcessedStripeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedStripeEventRepository
        extends JpaRepository<ProcessedStripeEvent, String> {
}
