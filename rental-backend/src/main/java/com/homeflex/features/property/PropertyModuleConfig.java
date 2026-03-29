package com.homeflex.features.property;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage(basePackages = "com.homeflex.features.property.domain")
@EnableJpaRepositories(basePackages = "com.homeflex.features.property.domain.repository")
@EnableElasticsearchRepositories(basePackages = "com.homeflex.features.property.domain.repository")
public class PropertyModuleConfig {
}
