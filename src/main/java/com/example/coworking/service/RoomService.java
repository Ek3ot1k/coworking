package com.example.coworking.service;

import com.example.coworking.dto.RoomDTO;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    public RoomService(RoomRepository roomRepository, ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.modelMapper = modelMapper;
    }

    public List<RoomEntity> findAll(){
        return roomRepository.findAll();
    }

    public RoomEntity findById(Long id){
        return roomRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Комната c id="+id+"не найдена"));
    }

    public RoomEntity addRoom(RoomEntity room){
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id){
        roomRepository.deleteById(id);
    }

    @Transactional
    public RoomEntity updateRoom(Long id,RoomDTO roomDTO){
        RoomEntity existingRoom=roomRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Комната с ID " + id + " не найдена"));
        modelMapper.map(roomDTO,existingRoom);
        return roomRepository.save(existingRoom);
    }
}
