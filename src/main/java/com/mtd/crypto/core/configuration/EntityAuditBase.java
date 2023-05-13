package com.mtd.crypto.core.configuration;


import static jakarta.persistence.TemporalType.TIMESTAMP;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import lombok.Getter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class EntityAuditBase {

    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    private Instant createdTime;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Instant updatedTime;

}
