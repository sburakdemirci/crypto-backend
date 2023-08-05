package com.mtd.crypto.core.user.data.entity;


import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.core.user.enumarator.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class User extends EntityAuditBase {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Authorities")
    @Enumerated(EnumType.STRING)
    List<Role> authorities;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotNull
    private String email;
    @NotNull
    private String name;
    @NotNull
    private String password;

    private boolean enabled;

    private boolean locked;

    private int failedLoginAttempts;


    @Builder
    public User(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.authorities = List.of(role);
    }
}
