package com.example.coworking.util;

import com.example.coworking.entity.UserEntity;
import com.example.coworking.repository.UserRepository; // Понадобится этот репозиторий
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserEntity user = (UserEntity) target;

        // Ищем пользователя в базе по email
        // Предполагается, что в репозитории есть метод findByEmail
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // Если нашли, значит такой email уже занят, выдаем ошибку
            errors.rejectValue("email", "", "Человек с таким email уже существует");
        }
    }
}