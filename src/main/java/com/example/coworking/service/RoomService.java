package com.example.coworking.service;

import com.example.coworking.dto.RoomDTO;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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
}
