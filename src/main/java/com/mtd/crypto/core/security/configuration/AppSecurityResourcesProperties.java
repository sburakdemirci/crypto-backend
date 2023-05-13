package com.mtd.crypto.core.security.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security.resources")
public class AppSecurityResourcesProperties {

    private List<String> unauthorizedPatterns;
}
