package com.example.coworking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record BookingRequestDTO(
        @NotNull(message = "ID комнаты обязателен")
        Long roomId,

        @NotNull(message = "Время начала обязательно")
        ZonedDateTime startTime,

        @NotNull(message = "Время окончания обязательно")
        ZonedDateTime endTime
) {}