package rw.kbms.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="bookings", indexes={@Index(name="idx_booking_trip_status",columnList="trip_id,status")})
public class Booking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne public UserAccount passenger; // null for walk-in passengers
 @ManyToOne(optional=false) public Trip trip;
 public String passengerName;
 public String passengerPhone;
 public String bookedByEmail;
 public Instant createdAt=Instant.now();
 public Instant paidAt;
 public Instant cancelledAt;
 @Column(nullable=false) public String status="PENDING_PAYMENT";
}
