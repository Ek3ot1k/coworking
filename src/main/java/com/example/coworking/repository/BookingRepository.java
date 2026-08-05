package com.example.coworking.repository;

import com.example.coworking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long> {
    List<BookingEntity> findBookingsByUserEmail(String email);
    List<BookingEntity> findBookingsByRoomId(Long roomId);
    @Query(value = """
        SELECT count (b.id)>0
        from bookings b 
        where b.room_id=:roomId
          and b.status='ACTIVE'
          and b.booking_period && cast(:timeRange as tstzrange)
    """,nativeQuery = true)
    boolean isRoomBusy(@Param("roomId") Long roomId,
                   @Param("timeRange") String timeRange);
}
