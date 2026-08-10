package com.example.coworking.service;

import com.example.coworking.entity.UserCredentialsEntity;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserEntityDetailsServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserEntityDetailsService userEntityDetailsService;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails(){
        String email = "test@test.com";
        String passwordHash = "ENCRYPTED_HASH_123";
        String role = "USER";

        UserEntity fakeUser=new UserEntity();
        fakeUser.setEmail(email);
        fakeUser.setRole(role);

        UserCredentialsEntity credentials=new UserCredentialsEntity();
        credentials.setPasswordHash(passwordHash);
        fakeUser.setCredentials(credentials);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(fakeUser));

        UserDetails result=userEntityDetailsService.loadUserByUsername(email);

        assertNotNull(result,"UserDetails не должен быть null");
        assertEquals(email,result.getUsername());
        assertEquals(passwordHash,result.getPassword());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void throw_UsernameNotFoundException_if_email_doesnt_exist_in_db(){
        String email="amin2005@mail.ru";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Executable action=()->userEntityDetailsService.loadUserByUsername(email);
        assertThrows(UsernameNotFoundException.class,action);
        verify(userRepository).findByEmail(email);
    }

}
