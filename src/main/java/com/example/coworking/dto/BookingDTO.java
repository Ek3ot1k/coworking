package com.example.coworking.dto;

import com.example.coworking.model.BookingStatus;
import java.time.ZonedDateTime;

public record BookingDTO(
        Long id,
        Long roomId,
        Long userId,
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        BookingStatus status
) {
}