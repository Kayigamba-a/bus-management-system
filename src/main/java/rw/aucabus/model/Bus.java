package rw.aucabus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "bus", uniqueConstraints = {
        @UniqueConstraint(name = "uk_bus_plate_number", columnNames = "plate_number")
})
public class Bus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_id")
    private Long busId;

    @NotBlank(message = "Plate number is required.")
    @Pattern(regexp = "^RA[A-Z]\\d{3}[A-Z]$",
            message = "Use Rwanda plate format such as RAD123B.")
    @Column(name = "plate_number", nullable = false, length = 15)
    private String plateNumber;

    @NotBlank(message = "Bus model is required.")
    @Size(min = 2, max = 50, message = "Model must contain between 2 and 50 characters.")
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @NotNull(message = "Capacity is required.")
    @Min(value = 10, message = "Capacity must be at least 10 seats.")
    @Max(value = 100, message = "Capacity cannot exceed 100 seats.")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "ACTIVE|MAINTENANCE|INACTIVE",
            message = "Status must be ACTIVE, MAINTENANCE, or INACTIVE.")
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    public Bus() {
    }

    public Bus(String plateNumber, String model, Integer capacity, String status) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
