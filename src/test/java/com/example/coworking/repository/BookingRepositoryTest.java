package com.example.coworking.repository;

import com.example.coworking.entity.BookingEntity; // Твоя сущность
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.model.BookingStatus;
import io.hypersistence.utils.hibernate.type.range.Range;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BookingRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void isRoomBusy_ShouldReturnTrue_WhenOverlapping(){
        BookingEntity booking = new BookingEntity();
        booking.setBookingPeriod(Range.closed(
                ZonedDateTime.parse("2026-08-10T10:00:00Z"),
                ZonedDateTime.parse("2026-08-10T12:00:00Z")
        ));
        booking.setStatus(BookingStatus.ACTIVE); // Обязательно, иначе запрос вернет false
        bookingRepository.save(booking);

        // Передаем строку в формате PostgreSQL диапазона
        String timeRange = "[2026-08-10 11:00:00+00:00, 2026-08-10 13:00:00+00:00]";
        boolean isBusy = bookingRepository.isRoomBusy(1L, timeRange);

        assertTrue(isBusy);
    }

    @Test
    void isRoomBusy_ShouldReturnFalse_WhenNotOverlapping(){
        BookingEntity booking = new BookingEntity();
        booking.setBookingPeriod(Range.closed(
                ZonedDateTime.parse("2026-08-10T10:00:00Z"),
                ZonedDateTime.parse("2026-08-10T12:00:00Z")
        ));
        booking.setStatus(BookingStatus.ACTIVE);
        bookingRepository.save(booking);

        String timeRange = "[2026-08-10 13:00:00+00:00, 2026-08-10 14:00:00+00:00]";
        boolean isBusy = bookingRepository.isRoomBusy(1L, timeRange);

        assertFalse(isBusy);
    }

    @Test
    void findBookingsByUserEmail_ShouldReturnList() {
        UserEntity user = new UserEntity();
        user.setEmail("amin2005@gmail.com");
        userRepository.save(user);

        RoomEntity room = new RoomEntity();
        roomRepository.save(room);

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setBookingPeriod(Range.closed(
                ZonedDateTime.parse("2026-08-10T10:00:00Z"),
                ZonedDateTime.parse("2026-08-10T12:00:00Z")
        ));
        bookingRepository.save(booking);

        List<BookingEntity> bookings = bookingRepository.findBookingsByUserEmail("amin2005@gmail.com");

        assertFalse(bookings.isEmpty());
    }

}
