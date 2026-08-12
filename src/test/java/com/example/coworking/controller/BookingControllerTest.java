package com.example.coworking.controller;

import com.example.coworking.dto.BookingDTO;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.security.JWTUtil;
import com.example.coworking.service.BookingService;
import com.example.coworking.service.UserEntityDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper=new ObjectMapper();

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private UserEntityDetailsService userEntityDetailsService;

    @MockitoBean
    private org.modelmapper.ModelMapper modelMapper;

    @MockitoBean
    private JWTUtil jwtUtil;

    @Test
    @WithMockUser
    void createBooking_ShouldReturnStatus201() throws Exception{
        BookingDTO requestDTO = new BookingDTO(
                null,                                // id
                1L,                                  // roomId
                null,                                // userId
                ZonedDateTime.now().plusHours(1),    // startTime
                ZonedDateTime.now().plusHours(3),    // endTime
                null                                 // status
        );

        mockMvc.perform(
                post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void whenResourceNotFound_ShouldReturnStatus404() throws Exception{
        when(bookingService.findById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Бронь не найдена"));
        mockMvc.perform(
                get("/api/v1/bookings/999")
        ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Бронь не найдена"));
    }

}
