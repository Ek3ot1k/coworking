package com.example.coworking.service;

import com.example.coworking.entity.UserEntity;
import com.example.coworking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(UserEntity user){
        String rawPassword=user.getCredentials().getPasswordHash();
        String encodedPassword=passwordEncoder.encode(rawPassword);
        user.getCredentials().setPasswordHash(encodedPassword);
        user.setRole("USER");
        userRepository.save(user);
    }
}
