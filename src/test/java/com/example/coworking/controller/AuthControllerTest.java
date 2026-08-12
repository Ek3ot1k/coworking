package com.example.coworking.controller;

import com.example.coworking.dto.UserDTO;
import com.example.coworking.entity.UserCredentialsEntity;
import com.example.coworking.security.JWTUtil;
import com.example.coworking.service.RegistrationService;
import com.example.coworking.service.UserEntityDetailsService;
import com.example.coworking.util.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    private UserEntityDetailsService userEntityDetailsService;

    @MockitoBean
    private org.modelmapper.ModelMapper modelMapper;

    @MockitoBean
    private UserValidator userValidator;

    @Test
    void register_WhenValidInput_ShouldReturnStatus200AndToken() throws Exception{
        UserDTO validRequest=new UserDTO("Amin", "Khuseynov", "amin2005@gmail.com", "password123");
        String fakeToken="jwt_token_12345";

        when(jwtUtil.generateToken(anyString())).thenReturn(fakeToken);

        mockMvc.perform(
                post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken));
    }

    @Test
    void register_WhenInvalidEmail_ShouldReturnStatusBadRequest() throws Exception{
        UserDTO badRequestDTO=new UserDTO("Amin", "Khuseynov", "", "password123");

        mockMvc.perform(
                post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequestDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenWrongPassword_ShouldReturnStatus401() throws Exception{
        UserDTO loginRequest=new UserDTO(null,null,"amin2005@gmail.com", "wrong_password");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Неверный логин или пароль"));
        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
        ).andExpect(status().isUnauthorized());

    }

}