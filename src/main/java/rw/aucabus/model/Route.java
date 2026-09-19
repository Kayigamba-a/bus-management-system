package rw.aucabus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @NotBlank(message = "Route name is required.")
    @Size(min = 3, max = 60, message = "Route name must contain between 3 and 60 characters.")
    @Column(name = "route_name", nullable = false, length = 60)
    private String routeName;

    @NotBlank(message = "Start point is required.")
    @Size(min = 2, max = 50, message = "Start point must contain between 2 and 50 characters.")
    @Column(name = "start_point", nullable = false, length = 50)
    private String startPoint;

    @NotBlank(message = "End point is required.")
    @Size(min = 2, max = 50, message = "End point must contain between 2 and 50 characters.")
    @Column(name = "end_point", nullable = false, length = 50)
    private String endPoint;

    @NotNull(message = "Distance is required.")
    @DecimalMin(value = "0.5", message = "Distance must be at least 0.5 km.")
    @DecimalMax(value = "300.0", message = "Distance cannot exceed 300 km.")
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @NotBlank(message = "Estimated duration is required.")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Duration must use HH:mm format, for example 01:30.")
    @Column(name = "estimated_duration", nullable = false, length = 5)
    private String estimatedDuration;

    @JsonIgnore
    @OneToMany(mappedBy = "route")
    private List<Schedule> schedules = new ArrayList<>();

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public String getStartPoint() { return startPoint; }
    public void setStartPoint(String startPoint) { this.startPoint = startPoint; }
    public String getEndPoint() { return endPoint; }
    public void setEndPoint(String endPoint) { this.endPoint = endPoint; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
}
