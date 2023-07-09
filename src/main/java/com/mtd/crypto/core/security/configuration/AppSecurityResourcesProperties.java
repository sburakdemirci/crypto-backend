package com.mtd.crypto.core.security.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security.resources")
public class AppSecurityResourcesProperties {

    private List<String> unauthorizedPatterns;
    private List<String> allowedOrigins;
}
