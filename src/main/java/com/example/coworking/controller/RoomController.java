package com.example.coworking.controller;

import com.example.coworking.dto.BookingDTO;
import com.example.coworking.dto.RoomDTO;
import com.example.coworking.entity.BookingEntity;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.service.BookingService;
import com.example.coworking.service.RoomService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final ModelMapper modelMapper;
    private final RoomService roomService;
    private final BookingService bookingService;

    public RoomController(ModelMapper modelMapper, RoomService roomService, BookingService bookingService) {
        this.modelMapper = modelMapper;
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @GetMapping()
    public List<RoomDTO> getRooms(){
        return roomService.findAll().stream().map(this::convertToRoomDTO).collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public RoomDTO getRoom(@PathVariable("id") Long id){
        return convertToRoomDTO(roomService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<RoomDTO> addRoom(@Valid @RequestBody RoomDTO roomDTO){
        RoomEntity room=convertToRoomEntity(roomDTO);
        RoomEntity savedRoom=roomService.addRoom(room);
        RoomDTO dto=convertToRoomDTO(savedRoom);
        log.info("Комната с id={} добавлена в базу",savedRoom.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id){
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable("id") Long id,
                                              @RequestBody RoomDTO roomDTO){
        RoomEntity room=roomService.updateRoom(id,roomDTO);
        RoomDTO dto=convertToRoomDTO(room);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{roomId}/bookings")
    public List<BookingDTO> getBookingsByRoom(@PathVariable("roomId") Long roomId){
        return bookingService.findBookingsByRoomId(roomId).stream()
                .map(this::convertToBookingDTO).collect(Collectors.toList());
    }


    public RoomEntity convertToRoomEntity(RoomDTO roomDTO) {
        RoomEntity room = new RoomEntity();
        room.setName(roomDTO.name());
        room.setCapacity(roomDTO.capacity());
        room.setActive(roomDTO.isActive());
        return room;
    }

    public RoomDTO convertToRoomDTO(RoomEntity room) {
        return new RoomDTO(
                room.getName(),
                room.getCapacity(),
                room.isActive()
        );
    }

    public BookingDTO convertToBookingDTO(BookingEntity booking){
        return new BookingDTO(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getUser().getId(),
                booking.getBookingPeriod().lower(), // достаем время начала из Range
                booking.getBookingPeriod().upper(), // достаем время окончания из Range
                booking.getStatus()
        );
    }
}
