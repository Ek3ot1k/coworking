package com.example.coworking.controller;

import com.example.coworking.dto.BookingDTO;
import com.example.coworking.dto.RoomDTO;
import com.example.coworking.entity.BookingEntity;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.service.BookingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/v1/bookings")
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
    public ResponseEntity<BookingDTO> addBooking(@Valid @RequestBody BookingDTO bookingDTO,
                                                 Principal principal){
        String userEmail=principal.getName();
        BookingEntity savedBooking=bookingService.createBooking(bookingDTO,userEmail);
        BookingDTO dto=convertToBookingDTO(savedBooking);
        log.info("Бронь с id={} добавлена в базу",savedBooking.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") Long id){
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable("id") Long id){
        BookingEntity booking =bookingService.cancel(id);
        return ResponseEntity.ok(convertToBookingDTO(booking));
    }

    @GetMapping("/my")
    public List<BookingDTO> getMyBookings(Principal principal){
        String userEmail=principal.getName();
        return bookingService.findBookingsByUserEmail(userEmail)
                .stream().map(this::convertToBookingDTO).collect(Collectors.toList());
    }

    @GetMapping("/room/{roomId}")
    public List<BookingDTO> getBookingsByRoom(@PathVariable("roomId") Long roomId){
        return bookingService.findBookingsByRoomId(roomId).stream()
                .map(this::convertToBookingDTO).collect(Collectors.toList());
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
