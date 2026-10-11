package rw.kbms.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.kbms.model.BusRoute;
public interface RouteRepository extends JpaRepository<BusRoute,Long> {}
