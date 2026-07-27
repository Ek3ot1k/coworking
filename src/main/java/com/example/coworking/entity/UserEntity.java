package com.example.coworking.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "first_name",nullable = false,length = 30)
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 30)
    private String lastName;

    @Column(name = "role",nullable = false,length = 30)
    private String role;

    @Column(name = "email",nullable = false,unique = true,length = 50)
    private String email;

    @Column(name = "created_at")
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private OffsetDateTime createdAt;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private UserCredentialsEntity credentials;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BookingEntity> bookings;

    public UserEntity() {
    }

    public void setCredentials(UserCredentialsEntity credentials){
        if (credentials==null){
            if(this.credentials!=null){
                this.credentials.setUser(null);
            }
        }else{
            credentials.setUser(this);
        }
        this.credentials=credentials;
    }

    public void addBooking(BookingEntity booking){
        bookings.add(booking);
        booking.setUser(this);
    }

    public void removeBooking(BookingEntity booking){
        bookings.remove(booking);
        booking.setUser(null);
    }
}
