package com.example.coworking.service;

import com.example.coworking.entity.UserCredentialsEntity;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    RegistrationService registrationService;

    @Captor
    ArgumentCaptor<UserEntity> userCaptor;

    @Test
    void register_ShouldEncryptPasswordBeforeSaving(){
        String rawPassword="my_secret_password";
        String encryptedPassword="ENCRYPTED_HASH_12345";

        UserEntity rawUser=new UserEntity();
        rawUser.setFirstName("Amin");
        rawUser.setLastName("Khuseynov");
        rawUser.setEmail("amin2005@gmail.com");

        UserCredentialsEntity credentials=new UserCredentialsEntity();
        credentials.setPasswordHash(rawPassword);
        rawUser.setCredentials(credentials);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        registrationService.register(rawUser,rawPassword);
        //Проверяем, что шифратор дергали
        verify(passwordEncoder).encode(rawPassword);
        //Ловим сущность на таможне перед базой
        verify(userRepository).save(userCaptor.capture());
        UserEntity savedUser=userCaptor.getValue();
        //Проверяем, что сервис САМ создал объект credentials
        assertNotNull(savedUser.getCredentials(),"Сервис должен был создать объект UserCredentialsEntity");
        //Проверяем, что в credentials лежит зашифрованный пароль (используем твое поле passwordHash)
        assertEquals(encryptedPassword,savedUser.getCredentials().getPasswordHash());
        //Проверяем двунаправленную связь (что credentials ссылается обратно на юзера)
        assertEquals(savedUser,savedUser.getCredentials().getUser());
        //Проверяем, что сервис не забыл выдать базовую роль!
        assertEquals("USER",savedUser.getRole());

    }



}
