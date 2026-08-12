package com.example.coworking.repository;

import com.example.coworking.entity.RoomEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class RoomRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void roomCrud_ShouldSaveUpdateAndDelete() {
        RoomEntity room = new RoomEntity();
        room.setName("Coworking A");
        // Если в RoomEntity есть другие поля, которые не могут быть null, укажи их здесь

        RoomEntity savedRoom = roomRepository.save(room);
        assertNotNull(savedRoom.getId());

        savedRoom.setName("Coworking B");
        roomRepository.save(savedRoom);

        RoomEntity updatedRoom = roomRepository.findById(savedRoom.getId()).orElseThrow();
        assertEquals("Coworking B", updatedRoom.getName());

        roomRepository.deleteById(savedRoom.getId());

        assertTrue(roomRepository.findById(savedRoom.getId()).isEmpty());
    }
}