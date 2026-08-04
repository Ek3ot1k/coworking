package com.example.coworking.entity;

import com.example.coworking.model.BookingStatus;
import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "room_id",referencedColumnName = "id",nullable = false)
    private RoomEntity room;

    @Type(PostgreSQLRangeType.class)
    @Getter
    @Setter
    @Column(name = "booking_period",columnDefinition = "tstzrange",nullable = false)
    private Range<ZonedDateTime> bookingPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private BookingStatus status=BookingStatus.PENDING_PAYMENT;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private OffsetDateTime createdAt;

    public BookingEntity() {
    }


}
