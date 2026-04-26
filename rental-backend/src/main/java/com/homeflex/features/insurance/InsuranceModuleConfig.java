package com.homeflex.features.insurance;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage(basePackages = "com.homeflex.features.insurance.domain.entity")
@EnableJpaRepositories(basePackages = "com.homeflex.features.insurance.domain.repository")
public class InsuranceModuleConfig {
}
