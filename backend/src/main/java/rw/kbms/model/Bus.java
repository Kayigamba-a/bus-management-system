package rw.kbms.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity @Table(name="buses") public class Bus {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @NotBlank @Column(nullable=false,unique=true) public String plateNumber;
 @NotBlank public String operatorName;
 @Min(1) @Max(100) public int capacity;
 public boolean active=true;
}
