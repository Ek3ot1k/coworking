package com.example.coworking.service;

import com.example.coworking.dto.BookingDTO;
import com.example.coworking.entity.BookingEntity;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.repository.BookingRepository;
import com.example.coworking.repository.RoomRepository;
import com.example.coworking.repository.UserRepository;
import io.hypersistence.utils.hibernate.type.range.Range;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
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

    public void delete(Long id){
        bookingRepository.deleteById(id);
    }

    @Transactional
    public BookingEntity cancel(Long id){
        BookingEntity booking=bookingRepository
                .findById(id).orElseThrow(()->new ResourceNotFoundException("Бронь не найдена"));
        booking.setStatus(BookingStatus.CANCELLED);
        return booking;
    }

    public List<BookingEntity> findBookingsByUserEmail(String email){
        return bookingRepository.findBookingsByUserEmail(email);
    }

    public List<BookingEntity> findBookingsByRoomId(Long roomId){
        return bookingRepository.findBookingsByRoomId(roomId);
    }

    @Transactional
    public BookingEntity createBooking(BookingDTO dto,String userEmail){
        UserEntity user=userRepository.findByEmail(userEmail)
                .orElseThrow(()->new ResourceNotFoundException("Пользователь не найден"));

        RoomEntity room=roomRepository.findById(dto.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Комната с ID " +
                        dto.roomId() + " не найдена"));

        Range<ZonedDateTime> timeRange=Range.closedOpen(dto.startTime(),dto.endTime());

        boolean isBusy=bookingRepository.isRoomBusy(room.getId(),timeRange.asString());
        if(isBusy){
            throw new IllegalStateException("Комната уже занята на выбранное время");
        }

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setBookingPeriod(timeRange);
        booking.setStatus(BookingStatus.ACTIVE);

        return bookingRepository.save(booking);
    }
}
