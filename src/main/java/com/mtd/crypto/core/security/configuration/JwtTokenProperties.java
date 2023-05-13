package com.mtd.crypto.core.security.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security.auth.jwt.token")
public class JwtTokenProperties {

    private String secretKey;
    private Long expireLength;

}
