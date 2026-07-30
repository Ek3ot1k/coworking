package com.example.coworking.controller;

import com.example.coworking.dto.RoomDTO;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.service.RoomService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/rooms")
public class RoomController {

    private final ModelMapper modelMapper;
    private final RoomService roomService;

    public RoomController(ModelMapper modelMapper, RoomService roomService) {
        this.modelMapper = modelMapper;
        this.roomService = roomService;
    }

    @GetMapping()
    public List<RoomDTO> getRooms(){
        return roomService.findAll().stream().map(this::convertToRoomDTO).collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public RoomDTO getRoom(@PathVariable("id") Long id){
        return convertToRoomDTO(roomService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<RoomDTO> addRoom(@Valid @RequestBody RoomDTO roomDTO){
        RoomEntity room=convertToRoomEntity(roomDTO);
        RoomEntity savedRoom=roomService.addRoom(room);
        RoomDTO dto=convertToRoomDTO(savedRoom);
        log.info("Комната с id={} добавлена в базу",savedRoom.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id){
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }


    public RoomEntity convertToRoomEntity(RoomDTO roomDTO){
        return modelMapper.map(roomDTO,RoomEntity.class);
    }

    public RoomDTO convertToRoomDTO(RoomEntity room){
        return modelMapper.map(room,RoomDTO.class);
    }
}
