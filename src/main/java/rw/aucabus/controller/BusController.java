package rw.aucabus.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.aucabus.model.Bus;
import rw.aucabus.service.BusService;
import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {
    private final BusService busService;
    public BusController(BusService busService) { this.busService = busService; }

    @PostMapping
    public ResponseEntity<Bus> create(@Valid @RequestBody Bus bus) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.create(bus));
    }

    @GetMapping
    public List<Bus> findAll() { return busService.findAll(); }

    @GetMapping("/{id}")
    public Bus findById(@PathVariable Long id) { return busService.findById(id); }

    @PutMapping("/{id}")
    public Bus update(@PathVariable Long id, @Valid @RequestBody Bus bus) { return busService.update(id, bus); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        busService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
