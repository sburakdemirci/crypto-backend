package com.mtd.crypto.core.security.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security.auth.jwt.token")
public class JwtTokenProperties {

    private String secretKey;
    private Long expireLength;

}
