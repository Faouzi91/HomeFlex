package com.homeflex.features.dispute;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage(basePackages = "com.homeflex.features.dispute.domain.entity")
@EnableJpaRepositories(basePackages = "com.homeflex.features.dispute.domain.repository")
public class DisputeModuleConfig {
}
