package rw.aucabus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.aucabus.exception.BusinessValidationException;
import rw.aucabus.exception.ResourceNotFoundException;
import rw.aucabus.model.Route;
import rw.aucabus.repository.RouteRepository;
import rw.aucabus.repository.ScheduleRepository;
import java.util.List;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;

    public RouteService(RouteRepository routeRepository, ScheduleRepository scheduleRepository) {
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Route create(Route route) {
        normalizeAndValidate(route);
        return routeRepository.save(route);
    }

    public List<Route> findAll() { return routeRepository.findAll(); }

    public Route findById(Long id) {
        return routeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Route not found with ID " + id));
    }

    @Transactional
    public Route update(Long id, Route input) {
        normalizeAndValidate(input);
        Route route = findById(id);
        route.setRouteName(input.getRouteName());
        route.setStartPoint(input.getStartPoint());
        route.setEndPoint(input.getEndPoint());
        route.setDistanceKm(input.getDistanceKm());
        route.setEstimatedDuration(input.getEstimatedDuration());
        return routeRepository.save(route);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        if (scheduleRepository.existsByRouteRouteId(id)) {
            throw new BusinessValidationException("This route cannot be deleted because it is assigned to a schedule.");
        }
        routeRepository.deleteById(id);
    }

    private void normalizeAndValidate(Route route) {
        if (route.getRouteName() != null) route.setRouteName(route.getRouteName().trim());
        if (route.getStartPoint() != null) route.setStartPoint(route.getStartPoint().trim());
        if (route.getEndPoint() != null) route.setEndPoint(route.getEndPoint().trim());
        if (route.getStartPoint() != null && route.getEndPoint() != null && route.getStartPoint().equalsIgnoreCase(route.getEndPoint())) {
            throw new BusinessValidationException("Start point and end point must be different.");
        }
    }
}
