package com.example.coworking.service;

import com.example.coworking.entity.UserEntity;
import com.example.coworking.repository.UserRepository;
import com.example.coworking.security.UserEntityDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserEntityDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public UserEntityDetailsService(UserRepository repository){
        this.repository=repository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user=repository.findByName(username);

        if(user.isEmpty()){
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return new UserEntityDetails(user.get());
    }
}
