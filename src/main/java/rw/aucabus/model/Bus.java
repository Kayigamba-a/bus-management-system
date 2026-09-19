package rw.aucabus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bus", uniqueConstraints = @UniqueConstraint(name = "uk_bus_plate_number", columnNames = "plate_number"))
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_id")
    private Long busId;

    @NotBlank(message = "Plate number is required.")
    @Pattern(regexp = "^RA[A-Z]\\d{3}[A-Z]$", message = "Use Rwanda plate format such as RAD123B.")
    @Column(name = "plate_number", nullable = false, length = 15)
    private String plateNumber;

    @NotBlank(message = "Bus model is required.")
    @Size(min = 2, max = 50, message = "Model must contain between 2 and 50 characters.")
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "Capacity is required.")
    @Min(value = 10, message = "Capacity must be at least 10 seats.")
    @Max(value = 100, message = "Capacity cannot exceed 100 seats.")
    @Column(nullable = false)
    private Integer capacity;

    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "ACTIVE|MAINTENANCE|INACTIVE", message = "Status must be ACTIVE, MAINTENANCE, or INACTIVE.")
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @JsonIgnore
    @OneToMany(mappedBy = "bus")
    private List<Schedule> schedules = new ArrayList<>();

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
}
