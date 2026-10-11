package rw.kbms.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
@Entity @Table(name="trips",indexes={@Index(name="idx_trip_departure",columnList="departureTime")}) public class Trip {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Bus bus;
 @ManyToOne(optional=false) public BusRoute route;
 @NotNull public LocalDateTime departureTime;
 @NotNull public LocalDateTime arrivalTime;
 @Column(nullable=false) public String status="SCHEDULED";
}
