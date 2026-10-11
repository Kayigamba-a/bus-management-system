package rw.kbms.controller;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import rw.kbms.repo.*;
import rw.kbms.model.*;
import rw.kbms.audit.*;
import rw.kbms.service.NotificationService;
import java.time.*;
import java.util.*;
@RestController @RequestMapping("/api") public class ManagementController {
 private final BusRepository buses;private final RouteRepository routes;private final TripRepository trips;private final BookingRepository bookings;private final UserRepository users;private final AuditRepository audit;private final NotificationService notify;
 public ManagementController(BusRepository b,RouteRepository r,TripRepository t,BookingRepository k,UserRepository u,AuditRepository a,NotificationService n){buses=b;routes=r;trips=t;bookings=k;users=u;audit=a;notify=n;}
 private void log(String action,Authentication auth,String details){audit.save(new AuditEvent(action,auth.getName(),details));}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","UP");}
 @GetMapping("/buses") public List<Bus> buses(){return buses.findAll();}
 @PostMapping("/buses") public Bus createBus(@Valid @RequestBody Bus bus,Authentication a){bus.id=null;Bus saved=buses.save(bus);log("BUS_CREATED",a,saved.plateNumber);return saved;}
 @PutMapping("/buses/{id}") public Bus editBus(@PathVariable Long id,@Valid @RequestBody Bus bus,Authentication a){if(!buses.existsById(id))throw new NotFound("Bus not found");bus.id=id;log("BUS_UPDATED",a,String.valueOf(id));return buses.save(bus);}
 @DeleteMapping("/buses/{id}") public void deleteBus(@PathVariable Long id,Authentication a){Bus b=buses.findById(id).orElseThrow(()->new NotFound("Bus not found"));b.active=false;buses.save(b);log("BUS_DISABLED",a,String.valueOf(id));}
 @GetMapping("/routes") public List<BusRoute> routes(){return routes.findAll();}
 @PostMapping("/routes") public BusRoute createRoute(@Valid @RequestBody BusRoute route,Authentication a){route.id=null;BusRoute saved=routes.save(route);log("ROUTE_CREATED",a,saved.routeCode);return saved;}
 @PutMapping("/routes/{id}") public BusRoute editRoute(@PathVariable Long id,@Valid @RequestBody BusRoute route,Authentication a){if(!routes.existsById(id))throw new NotFound("Route not found");route.id=id;log("ROUTE_UPDATED",a,String.valueOf(id));return routes.save(route);}
 @DeleteMapping("/routes/{id}") public void disableRoute(@PathVariable Long id,Authentication a){BusRoute r=routes.findById(id).orElseThrow(()->new NotFound("Route not found"));r.active=false;routes.save(r);log("ROUTE_DISABLED",a,String.valueOf(id));}
 @GetMapping("/trips") public List<Trip> trips(){return trips.findByDepartureTimeAfterOrderByDepartureTimeAsc(LocalDateTime.now().minusDays(1));}
 public record TripRequest(Long busId,Long routeId,LocalDateTime departureTime,LocalDateTime arrivalTime,String status){}
 private Trip mapTrip(Trip trip,TripRequest req){trip.bus=buses.findById(req.busId()).orElseThrow(()->new NotFound("Bus not found"));trip.route=routes.findById(req.routeId()).orElseThrow(()->new NotFound("Route not found"));if(!trip.bus.active || !trip.route.active)throw new IllegalArgumentException("Bus and route must be active");if(req.departureTime()==null||req.arrivalTime()==null||!req.arrivalTime().isAfter(req.departureTime()))throw new IllegalArgumentException("Arrival must be after departure");trip.departureTime=req.departureTime();trip.arrivalTime=req.arrivalTime();trip.status=req.status()==null?"SCHEDULED":req.status();if(!List.of("SCHEDULED","DELAYED","CANCELLED","COMPLETED").contains(trip.status))throw new IllegalArgumentException("Invalid trip status");return trip;}
 @PostMapping("/trips") public Trip createTrip(@RequestBody TripRequest req,Authentication a){Trip saved=trips.save(mapTrip(new Trip(),req));log("TRIP_CREATED",a,String.valueOf(saved.id));notify.send("Trip scheduled: "+saved.route.routeCode+" "+saved.departureTime);return saved;}
 @PutMapping("/trips/{id}") public Trip updateTrip(@PathVariable Long id,@RequestBody TripRequest req,Authentication a){Trip t=trips.findById(id).orElseThrow(()->new NotFound("Trip not found"));Trip saved=trips.save(mapTrip(t,req));log("TRIP_UPDATED",a,String.valueOf(id));notify.send("Trip updated: "+id+" / "+saved.status);return saved;}
 @DeleteMapping("/trips/{id}") public Trip cancelTrip(@PathVariable Long id,Authentication a){Trip t=trips.findById(id).orElseThrow(()->new NotFound("Trip not found"));t.status="CANCELLED";log("TRIP_CANCELLED",a,String.valueOf(id));notify.send("Trip cancelled: "+id);return trips.save(t);}
 @GetMapping("/admin/users") public List<UserAccount> allUsers(){return users.findAll();}
 public record RoleRequest(Role role){}
 @PutMapping("/admin/users/{id}/role") public UserAccount setRole(@PathVariable Long id,@RequestBody RoleRequest req,Authentication a){UserAccount u=users.findById(id).orElseThrow(()->new NotFound("User not found"));if(req.role()==null)throw new IllegalArgumentException("Role is required");if(u.email.equals(a.getName())&&req.role()!=Role.ADMIN)throw new IllegalArgumentException("Cannot remove your own admin role");u.role=req.role();log("ROLE_CHANGED",a,id+":"+req.role());return users.save(u);}
 @GetMapping("/admin/audit") public List<AuditEvent> audit(){return audit.findAll();}
 @ResponseStatus(HttpStatus.NOT_FOUND) static class NotFound extends RuntimeException {NotFound(String msg){super(msg);}}
}
