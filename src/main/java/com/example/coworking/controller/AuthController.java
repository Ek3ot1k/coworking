package com.example.coworking.controller;

import com.example.coworking.dto.UserDTO;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.security.JWTUtil;
import com.example.coworking.service.RegistrationService;
import com.example.coworking.util.UserValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jWTUtil;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;
    private final RegistrationService registrationService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JWTUtil jWTUtil,
                          ModelMapper modelMapper,
                          UserValidator userValidator,
                          RegistrationService registrationService) {
        this.authenticationManager = authenticationManager;
        this.jWTUtil = jWTUtil;
        this.modelMapper = modelMapper;
        this.userValidator = userValidator;
        this.registrationService = registrationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> loginRequest){
        String email=loginRequest.get("email");
        try{
            log.info("Попытка входа пользователя с email: {}",email);
            String password=loginRequest.get("password");

            Authentication authentication=authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email,password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Пользователь {} успешно авторизован",email);
            String token=jWTUtil.generateToken(email);

            return ResponseEntity.ok(Map.of("jwt-token",token));
        }catch (Exception e) {
            log.error("Ошибка авторизация пользователя {}:{}",email,e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> performRegistration(@RequestBody @Valid UserDTO userDTO,
                                                 BindingResult bindingResult){
        UserEntity user=convertToUser(userDTO);
        userValidator.validate(user,bindingResult);

        if(bindingResult.hasErrors()){
            String errors=bindingResult.getFieldErrors().stream()
                    .map(e->e.getField()+": "+e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(Map.of("message","Ошибка валидации: "+errors));
        }

        registrationService.register(user);
        String token=jWTUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("jwt-token",token));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotReadable(Exception e) {
        return Map.of("error", "Invalid JSON: " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    private UserEntity convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, UserEntity.class);
    }
}








