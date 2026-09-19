package rw.aucabus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.aucabus.model.Bus;

public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByPlateNumberIgnoreCase(String plateNumber);
    boolean existsByPlateNumberIgnoreCaseAndBusIdNot(String plateNumber, Long busId);
}
