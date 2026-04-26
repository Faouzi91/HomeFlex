package com.homeflex.features.finance;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage(basePackages = "com.homeflex.features.finance.domain.entity")
@EnableJpaRepositories(basePackages = "com.homeflex.features.finance.domain.repository")
public class FinanceModuleConfig {
}
