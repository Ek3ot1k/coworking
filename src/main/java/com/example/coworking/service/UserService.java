package com.example.coworking.service;

import com.example.coworking.entity.UserEntity;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Пользователь с id="+id+" не найден"));
    }

    public UserEntity findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Пользователь не найден"));
    }
}
