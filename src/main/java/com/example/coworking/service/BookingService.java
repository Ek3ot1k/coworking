package com.example.coworking.service;

import com.example.coworking.entity.BookingEntity;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<BookingEntity> findAll(){
        return bookingRepository.findAll();
    }

    public BookingEntity findById(Long id){
        return bookingRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Бронь не найдена"));
    }

    public BookingEntity save(BookingEntity booking){
        return bookingRepository.save(booking);
    }
}
