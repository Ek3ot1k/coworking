package com.example.coworking.dto;

import com.example.coworking.entity.RoomEntity;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.model.BookingStatus;

public record BookingDTO(UserEntity user, RoomEntity room, BookingStatus status) {
}
