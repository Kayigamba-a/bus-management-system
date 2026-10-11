package rw.kbms.repo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import rw.kbms.model.Trip;
import java.util.*;
public interface TripRepository extends JpaRepository<Trip,Long> {
 List<Trip> findByDepartureTimeAfterOrderByDepartureTimeAsc(java.time.LocalDateTime time);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from Trip t where t.id=:id") Optional<Trip> lockById(@Param("id") Long id);
}
