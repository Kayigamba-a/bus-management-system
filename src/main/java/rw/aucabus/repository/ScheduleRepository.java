package rw.aucabus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.aucabus.model.Schedule;
import java.time.LocalTime;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByBusBusId(Long busId);
    boolean existsByRouteRouteId(Long routeId);
    boolean existsByBusBusIdAndDayOfOperationAndDepartureTime(Long busId, String dayOfOperation, LocalTime departureTime);
    boolean existsByBusBusIdAndDayOfOperationAndDepartureTimeAndScheduleIdNot(Long busId, String dayOfOperation, LocalTime departureTime, Long scheduleId);
}
