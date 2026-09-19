package rw.aucabus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;

public class ScheduleRequest {
    @NotNull(message = "Bus ID is required.")
    private Long busId;

    @NotNull(message = "Route ID is required.")
    private Long routeId;

    @NotNull(message = "Departure time is required.")
    private LocalTime departureTime;

    @NotNull(message = "Arrival time is required.")
    private LocalTime arrivalTime;

    @NotBlank(message = "Day of operation is required.")
    @Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY", message = "Use a valid uppercase day of the week.")
    private String dayOfOperation;

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getDayOfOperation() { return dayOfOperation; }
    public void setDayOfOperation(String dayOfOperation) { this.dayOfOperation = dayOfOperation; }
}
