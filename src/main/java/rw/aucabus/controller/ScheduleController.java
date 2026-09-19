package rw.aucabus.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.aucabus.dto.ScheduleRequest;
import rw.aucabus.model.Schedule;
import rw.aucabus.service.ScheduleService;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;
    public ScheduleController(ScheduleService scheduleService) { this.scheduleService = scheduleService; }

    @PostMapping
    public ResponseEntity<Schedule> create(@Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(request));
    }

    @GetMapping
    public List<Schedule> findAll() { return scheduleService.findAll(); }

    @GetMapping("/{id}")
    public Schedule findById(@PathVariable Long id) { return scheduleService.findById(id); }

    @PutMapping("/{id}")
    public Schedule update(@PathVariable Long id, @Valid @RequestBody ScheduleRequest request) { return scheduleService.update(id, request); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
