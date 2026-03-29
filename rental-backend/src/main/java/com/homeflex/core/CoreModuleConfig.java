package com.homeflex.core;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage(basePackages = {
        "com.homeflex.core.domain",
        "com.homeflex.core.domain.event"
})
@EnableJpaRepositories(basePackages = {
        "com.homeflex.core.domain.repository",
        "com.homeflex.core.domain.event"
})
public class CoreModuleConfig {
}
