package com.example.coworking.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
public class UserCredentialsEntity {
    @Id
    @Column(name = "user_id")
    @Setter(AccessLevel.NONE)
    private Long userId;

    @Column(name = "password_hash",nullable = false,length = 255)
    private String passwordHash;

    @Column(name = "updated_at",nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId
    @JoinColumn(name = "user_id",nullable = false)
    private UserEntity user;

    public UserCredentialsEntity() {
    }
}
