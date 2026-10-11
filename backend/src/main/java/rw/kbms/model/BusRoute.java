package rw.kbms.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity @Table(name="bus_routes") public class BusRoute {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @NotBlank public String origin;
 @NotBlank public String destination;
 @NotBlank @Column(unique=true) public String routeCode;
 @Min(1) public int durationMinutes;
 @DecimalMin("0.0") public double fareRwf;
 public boolean active=true;
}
