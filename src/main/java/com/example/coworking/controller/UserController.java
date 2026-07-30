package com.example.coworking.controller;

import com.example.coworking.dto.UserDTO;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable("id") Long id){
        return convertToUserDTO(userService.findById(id));
    }

    private UserDTO convertToUserDTO(UserEntity user){
        return modelMapper.map(user, UserDTO.class);
    }

    private UserEntity convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, UserEntity.class);
    }
}
