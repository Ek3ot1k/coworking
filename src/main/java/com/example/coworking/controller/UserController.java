package com.example.coworking.controller;

import com.example.coworking.dto.BookingDTO;
import com.example.coworking.dto.UserDTO;
import com.example.coworking.entity.BookingEntity;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.service.BookingService;
import com.example.coworking.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final BookingService bookingService;

    public UserController(UserService userService, ModelMapper modelMapper, BookingService bookingService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable("id") Long id){
        return convertToUserDTO(userService.findById(id));
    }

    @GetMapping("/{id}/bookings")
    public List<BookingDTO> getUsersBookings(@PathVariable("id") Long id){
        List<BookingEntity> userBookings=bookingService.findBookingsByUserId(id);

        return userBookings.stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/me")
    public UserDTO getMyProfile(Principal principal){
        String email=principal.getName();
        UserEntity currentUser=userService.findByEmail(email);
        return convertToUserDTO(currentUser);
    }

    private UserDTO convertToUserDTO(UserEntity user){
        // Мапим вручную, так как UserDTO — это record (без пустого конструктора)
        return new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null // пароль наружу отдавать не нужно, поэтому передаем null
        );
    }

    private BookingDTO convertToBookingDTO(BookingEntity booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getRoom().getId(), // Предполагаю, что связь настроена так
                booking.getUser().getId(),
                booking.getBookingPeriod().lower(), // Начало брони из твоего Range
                booking.getBookingPeriod().upper(), // Конец брони
                booking.getStatus()
        );
    }
}
