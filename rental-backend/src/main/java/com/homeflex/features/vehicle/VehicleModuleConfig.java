package com.homeflex.features.vehicle;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage(basePackages = "com.homeflex.features.vehicle.domain.entity")
@EnableJpaRepositories(basePackages = "com.homeflex.features.vehicle.domain.repository")
public class VehicleModuleConfig {
}
