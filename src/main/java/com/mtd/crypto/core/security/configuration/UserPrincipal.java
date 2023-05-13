package com.mtd.crypto.core.security.configuration;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mtd.crypto.core.user.data.entity.User;


/*
public class UserPrincipal implements OAuth2User, UserDetails {
*/
public class UserPrincipal implements UserDetails {

    private final User user;


    public UserPrincipal(User user) {
        this.user = user;

    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(user);
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
