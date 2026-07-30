package com.example.coworking.dto;

import com.example.coworking.entity.UserCredentialsEntity;

public record UserDTO(String firstName,
                      String lastName,
                      String email,
                      String password) {
}
