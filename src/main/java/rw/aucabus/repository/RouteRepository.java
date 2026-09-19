package rw.aucabus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.aucabus.model.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
