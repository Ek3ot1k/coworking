package com.example.coworking.service;

import com.example.coworking.dto.BookingDTO;
import com.example.coworking.dto.RoomDTO;
import com.example.coworking.entity.BookingEntity;
import com.example.coworking.entity.RoomEntity;
import com.example.coworking.entity.UserEntity;
import com.example.coworking.exceptions.ResourceNotFoundException;
import com.example.coworking.model.BookingStatus;
import com.example.coworking.repository.BookingRepository;
import com.example.coworking.repository.RoomRepository;
import com.example.coworking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    RoomRepository roomRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingService bookingService;

    @Test
    void createBooking_WhenRoomIsFree_ShouldSaveAndReturnBooking(){
        String userEmail="test@test.com";
        Long roomId=1L;

        UserEntity fakeUser=new UserEntity();
        RoomEntity fakeRoom=new RoomEntity();

        ZonedDateTime startTime=ZonedDateTime.now().plusDays(1).withHour(10);
        ZonedDateTime endTime=startTime.plusHours(2);
        BookingDTO dto=new BookingDTO(null,roomId,null,startTime,endTime,null);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(fakeUser));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(fakeRoom));

        when(bookingRepository.isRoomBusy(eq(fakeRoom.getId()),anyString())).thenReturn(false);

        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(new BookingEntity());

        BookingEntity result=bookingService.createBooking(dto,userEmail);

        assertNotNull(result);

        verify(bookingRepository).save(any(BookingEntity.class));

        verify(bookingRepository).isRoomBusy(eq(fakeRoom.getId()),anyString());
    }

    @Test
    void throw_IllegalStateException(){
        String userEmail="test@test.com";
        Long roomId=1L;

        RoomEntity fakeRoom=new RoomEntity();
        UserEntity fakeUser=new UserEntity();
        fakeRoom.setId(roomId);

        ZonedDateTime startTime = ZonedDateTime.now().plusDays(1).withHour(10);
        ZonedDateTime endTime = startTime.plusHours(2);
        BookingDTO dto=new BookingDTO(null,roomId,null,startTime,endTime,null);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(fakeUser));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(fakeRoom));
        when(bookingRepository.isRoomBusy(eq(fakeRoom.getId()),anyString())).thenReturn(true);

        Executable action=()->bookingService.createBooking(dto,userEmail);
        IllegalStateException exception=assertThrows(IllegalStateException.class,action);
        assertEquals("Комната уже занята на выбранное время", exception.getMessage());
        verify(bookingRepository,never()).save(any(BookingEntity.class));
    }

    @Test
    void throw_ResourceNotFoundException(){
        String userEmail="ghost@test.com";
        Long roomId=1L;

        BookingDTO dto=new BookingDTO(null,roomId,null,
                ZonedDateTime.now(),ZonedDateTime.now().withHour(1),null);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        Executable action=()->bookingService.createBooking(dto,userEmail);
        assertThrows(ResourceNotFoundException.class,action);
        verify(bookingRepository,never()).save(any(BookingEntity.class));
    }

    @Test
    void change_status_to_cancelled(){
        Long bookingId=99L;

        BookingEntity fakeBooking=new BookingEntity();
        fakeBooking.setId(bookingId);
        fakeBooking.setStatus(BookingStatus.ACTIVE);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(fakeBooking));

        bookingService.cancel(bookingId);

        assertEquals(fakeBooking.getStatus(),BookingStatus.CANCELLED);
    }

}








