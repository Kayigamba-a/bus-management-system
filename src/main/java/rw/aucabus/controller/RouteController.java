package rw.aucabus.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.aucabus.model.Route;
import rw.aucabus.service.RouteService;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    private final RouteService routeService;
    public RouteController(RouteService routeService) { this.routeService = routeService; }

    @PostMapping
    public ResponseEntity<Route> create(@Valid @RequestBody Route route) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(route));
    }

    @GetMapping
    public List<Route> findAll() { return routeService.findAll(); }

    @GetMapping("/{id}")
    public Route findById(@PathVariable Long id) { return routeService.findById(id); }

    @PutMapping("/{id}")
    public Route update(@PathVariable Long id, @Valid @RequestBody Route route) { return routeService.update(id, route); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
