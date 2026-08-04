package com.example.coworking.controller;

import com.example.coworking.dto.BookingDTO;
import com.example.coworking.entity.BookingEntity;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.service.BookingService;
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
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    public BookingController(BookingService bookingService,
                             ModelMapper modelMapper) {
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<BookingDTO> getBookings(){
        return bookingService.findAll().stream().map(this::convertToBookingDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BookingDTO getBooking(@PathVariable("id") Long id){
        return convertToBookingDTO(bookingService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookingDTO> addBooking(@Valid @RequestBody BookingDTO bookingDTO){
        BookingEntity booking=convertToBooking(bookingDTO);
        BookingEntity savedRoom=bookingService.save(booking);
        BookingDTO dto=convertToBookingDTO(savedRoom);
        log.info("Бронь с id={} добавлена в базу",savedRoom.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    public BookingDTO convertToBookingDTO(BookingEntity booking){
        return modelMapper.map(booking, BookingDTO.class);
    }

    public BookingEntity convertToBooking(BookingDTO bookingDTO){
        return modelMapper.map(bookingDTO, BookingEntity.class);
    }
}
