package rw.kbms.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rw.kbms.model.Booking;
import java.util.List;
public interface BookingRepository extends JpaRepository<Booking,Long> {
 List<Booking> findByPassengerIdOrderByCreatedAtDesc(Long id);
 List<Booking> findByBookedByEmailOrderByCreatedAtDesc(String email);
 List<Booking> findAllByOrderByCreatedAtDesc();
 @Query("select count(b) from Booking b where b.trip.id=:trip and b.status in ('PENDING_PAYMENT','CONFIRMED')")
 long occupied(@Param("trip") Long trip);
 @Query("select count(b) from Booking b where b.trip.id=:trip and b.passenger.id=:user and b.status in ('PENDING_PAYMENT','CONFIRMED')")
 long activeForUser(@Param("trip") Long trip,@Param("user") Long user);
 @Query("select count(b) from Booking b where b.trip.id=:trip and b.passengerPhone=:phone and b.status in ('PENDING_PAYMENT','CONFIRMED')")
 long activeForPhone(@Param("trip") Long trip,@Param("phone") String phone);
 @Query("select b from Booking b where b.status='PENDING_PAYMENT'") List<Booking> pending();
}
