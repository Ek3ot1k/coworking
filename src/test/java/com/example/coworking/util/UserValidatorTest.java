package com.example.coworking.util;

import com.example.coworking.entity.UserEntity;
import com.example.coworking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    @Mock
    UserRepository userRepository;

    @Mock
    Errors errors;

    @InjectMocks
    UserValidator userValidator;

    @Test
    void validate_WhenEmailAlreadyExists_ShouldRejectValue(){
        String email = "occupied@test.com";
        UserEntity targetUser = new UserEntity();
        targetUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));
        userValidator.validate(targetUser,errors);
        verify(errors).rejectValue("email", "", "Человек с таким email уже существует");

    }

    @Test
    void validate_WhenEmailIsUnique_ShouldNotRejectValue(){
        String email = "free@test.com";
        UserEntity targetUser = new UserEntity();
        targetUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        userValidator.validate(targetUser,errors);

        verify(errors,never()).rejectValue(anyString(),anyString(),anyString());
    }
}
