package com.example.coworking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "rooms")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @Column(name = "capacity")
    private int capacity;

    @Getter
    @Setter
    @Column(name = "is_active")
    private boolean isActive;

    @Getter
    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    public long getId() {
        return id;
    }

}
