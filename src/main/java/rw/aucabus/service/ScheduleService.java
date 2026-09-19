package rw.aucabus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.aucabus.dto.ScheduleRequest;
import rw.aucabus.exception.BusinessValidationException;
import rw.aucabus.exception.ResourceNotFoundException;
import rw.aucabus.model.Bus;
import rw.aucabus.model.Route;
import rw.aucabus.model.Schedule;
import rw.aucabus.repository.BusRepository;
import rw.aucabus.repository.RouteRepository;
import rw.aucabus.repository.ScheduleRepository;
import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, BusRepository busRepository, RouteRepository routeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public Schedule create(ScheduleRequest request) {
        validateTimes(request);
        Bus bus = getBus(request.getBusId());
        Route route = getRoute(request.getRouteId());
        validateBus(bus);
        String day = request.getDayOfOperation().trim().toUpperCase();
        if (scheduleRepository.existsByBusBusIdAndDayOfOperationAndDepartureTime(bus.getBusId(), day, request.getDepartureTime())) {
            throw new BusinessValidationException("This bus already has a schedule at the same departure time on " + day + ".");
        }
        Schedule schedule = new Schedule();
        apply(schedule, request, bus, route, day);
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> findAll() { return scheduleRepository.findAll(); }

    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Schedule not found with ID " + id));
    }

    @Transactional
    public Schedule update(Long id, ScheduleRequest request) {
        validateTimes(request);
        Schedule schedule = findById(id);
        Bus bus = getBus(request.getBusId());
        Route route = getRoute(request.getRouteId());
        validateBus(bus);
        String day = request.getDayOfOperation().trim().toUpperCase();
        if (scheduleRepository.existsByBusBusIdAndDayOfOperationAndDepartureTimeAndScheduleIdNot(bus.getBusId(), day, request.getDepartureTime(), id)) {
            throw new BusinessValidationException("This bus already has another schedule at the same departure time on " + day + ".");
        }
        apply(schedule, request, bus, route, day);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        scheduleRepository.deleteById(id);
    }

    private Bus getBus(Long id) {
        return busRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID " + id));
    }

    private Route getRoute(Long id) {
        return routeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Route not found with ID " + id));
    }

    private void validateTimes(ScheduleRequest request) {
        if (request.getDepartureTime() != null && request.getArrivalTime() != null && !request.getArrivalTime().isAfter(request.getDepartureTime())) {
            throw new BusinessValidationException("Arrival time must be after departure time.");
        }
    }

    private void validateBus(Bus bus) {
        if (!"ACTIVE".equalsIgnoreCase(bus.getStatus())) {
            throw new BusinessValidationException("Only a bus with ACTIVE status can be assigned to a schedule.");
        }
    }

    private void apply(Schedule schedule, ScheduleRequest request, Bus bus, Route route, String day) {
        schedule.setBus(bus);
        schedule.setRoute(route);
        schedule.setDepartureTime(request.getDepartureTime());
        schedule.setArrivalTime(request.getArrivalTime());
        schedule.setDayOfOperation(day);
    }
}
