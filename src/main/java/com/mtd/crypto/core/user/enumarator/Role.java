package com.mtd.crypto.core.user.enumarator;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_CRON;

    @Override
    public String getAuthority() {
        return name();
    }
}
