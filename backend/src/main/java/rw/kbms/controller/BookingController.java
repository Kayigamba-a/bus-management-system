package rw.kbms.controller;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rw.kbms.repo.*;
import rw.kbms.model.*;
import rw.kbms.audit.*;
import rw.kbms.service.NotificationService;

@RestController @RequestMapping("/api/bookings")
public class BookingController {
 private final BookingService service;
 public BookingController(BookingService service){this.service=service;}
 public record CreateRequest(Long passengerId,String passengerName,String passengerPhone){}
 @GetMapping("/mine") public List<Booking> mine(Authentication a){return service.mine(a);}
 @GetMapping("/admin/all") public List<Booking> all(){return service.all();}
 @PostMapping("/{tripId}") public ResponseEntity<?> book(@PathVariable Long tripId,@RequestBody(required=false) CreateRequest req,Authentication a){return ResponseEntity.status(201).body(service.create(tripId,req,a));}
 @PostMapping("/{id}/cancel") public Booking cancel(@PathVariable Long id,Authentication a){return service.cancel(id,a);}
 @PostMapping("/{id}/pay") public Booking confirm(@PathVariable Long id,Authentication a){return service.pay(id,a);}
}

@Service class BookingService {
 private final BookingRepository bookings;private final TripRepository trips;private final UserRepository users;private final AuditRepository audit;private final NotificationService notification;
 private static final ZoneId KIGALI=ZoneId.of("Africa/Kigali");
 private static final String REMINDER="Please arrive to pay at least 30 minutes before departure, otherwise your reserved seat will be released for another passenger.";
 BookingService(BookingRepository b,TripRepository t,UserRepository u,AuditRepository a,NotificationService n){bookings=b;trips=t;users=u;audit=a;notification=n;}
 private UserAccount caller(Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
 private boolean staff(UserAccount u){return u.role==Role.ADMIN;}
 private boolean admin(UserAccount u){return u.role==Role.ADMIN;}
 private RuntimeException conflict(String message){return new BookingConflict(message);}
 private void log(String action,Authentication a,String details){audit.save(new AuditEvent(action,a.getName(),details));}
 private void expireForTrip(Long tripId){for(Booking b:bookings.pending())if(b.trip.id.equals(tripId)&&!LocalDateTime.now(KIGALI).isBefore(b.trip.departureTime.minusMinutes(30))){b.status="EXPIRED";bookings.save(b);}}
 @Transactional public Booking create(Long tripId,BookingController.CreateRequest req,Authentication auth){
  UserAccount actor=caller(auth);
  Trip trip=trips.lockById(tripId).orElseThrow(()->new BookingNotFound("Trip not found"));
  expireForTrip(tripId);
  if(!List.of("SCHEDULED","DELAYED").contains(trip.status))throw conflict("This trip cannot be booked: "+trip.status);
  if(!LocalDateTime.now(KIGALI).isBefore(trip.departureTime.minusMinutes(30)))throw conflict("Booking closed 30 minutes before departure");
  UserAccount passenger=null;String name,phone;
  if(actor.role==Role.OPERATOR)throw new org.springframework.security.access.AccessDeniedException("Operators cannot book seats");
  if(staff(actor)){
   if(req==null)throw new IllegalArgumentException("Choose a registered passenger or enter a walk-in passenger name and phone");
   if(req.passengerId()!=null){passenger=users.findById(req.passengerId()).orElseThrow(()->new BookingNotFound("Passenger not found"));if(passenger.role!=Role.PASSENGER)throw new IllegalArgumentException("Select a passenger account, not an admin/operator");name=passenger.name;phone=req.passengerPhone()==null?"":req.passengerPhone().trim();if(phone.isBlank())throw new IllegalArgumentException("Passenger phone is required");}
   else {name=req.passengerName()==null?"":req.passengerName().trim();phone=req.passengerPhone()==null?"":req.passengerPhone().trim();if(name.isBlank()||phone.isBlank())throw new IllegalArgumentException("Walk-in passenger name and phone are required");}
  }else {passenger=actor;name=actor.name;phone=req!=null&&req.passengerPhone()!=null?req.passengerPhone().trim():"";if(phone.isBlank())throw new IllegalArgumentException("Please provide your phone number");}
  if(!phone.matches("[+0-9][0-9 -]{7,17}"))throw new IllegalArgumentException("Enter a valid phone number");
  if(passenger!=null && bookings.activeForUser(tripId,passenger.id)>0)throw conflict("This passenger already has an active booking for this trip");
  if(bookings.activeForPhone(tripId,phone)>0)throw conflict("This phone number already has an active booking for this trip");
  if(bookings.occupied(tripId)>=trip.bus.capacity)throw conflict("Bus is full");
  Booking b=new Booking();b.passenger=passenger;b.passengerName=name;b.passengerPhone=phone;b.bookedByEmail=actor.email;b.trip=trip;
  Booking saved=bookings.saveAndFlush(b);log("BOOKING_CREATED",auth,saved.id+" for "+name);
  notification.send("Booking #"+saved.id+" pending payment. "+REMINDER);
  return saved;
 }
 @Transactional public Booking cancel(Long id,Authentication auth){UserAccount actor=caller(auth);Booking b=bookings.findById(id).orElseThrow(()->new BookingNotFound("Booking not found"));trips.lockById(b.trip.id).orElseThrow();b=bookings.findById(id).orElseThrow();if(!admin(actor)&&!(b.passenger!=null&&b.passenger.id.equals(actor.id)))throw new org.springframework.security.access.AccessDeniedException("Not your booking");if(!List.of("PENDING_PAYMENT","CONFIRMED").contains(b.status))throw conflict("Booking is not active");if(!LocalDateTime.now(KIGALI).isBefore(b.trip.departureTime))throw conflict("Trip already departed");b.status="CANCELLED";b.cancelledAt=Instant.now();Booking saved=bookings.save(b);log("BOOKING_CANCELLED",auth,id.toString());return saved;}
 @Transactional public Booking pay(Long id,Authentication auth){UserAccount actor=caller(auth);if(!admin(actor))throw new org.springframework.security.access.AccessDeniedException("Admin only");Booking b=bookings.findById(id).orElseThrow(()->new BookingNotFound("Booking not found"));trips.lockById(b.trip.id).orElseThrow();expireForTrip(b.trip.id);b=bookings.findById(id).orElseThrow();if(!"PENDING_PAYMENT".equals(b.status))throw conflict("Booking cannot be paid: "+b.status);b.status="CONFIRMED";b.paidAt=Instant.now();Booking saved=bookings.save(b);log("BOOKING_PAID",auth,id.toString());return saved;}
 @Transactional(readOnly=true) public List<Booking> mine(Authentication a){UserAccount u=caller(a);return u.role==Role.ADMIN ? bookings.findAllByOrderByCreatedAtDesc() : bookings.findByPassengerIdOrderByCreatedAtDesc(u.id);}
 @Transactional(readOnly=true) public List<Booking> all(){return bookings.findAllByOrderByCreatedAtDesc();}
 @Scheduled(fixedDelay=60000) @Transactional public void expire(){for(Booking b:bookings.pending()) if(!LocalDateTime.now(KIGALI).isBefore(b.trip.departureTime.minusMinutes(30))){trips.lockById(b.trip.id).orElseThrow();Booking fresh=bookings.findById(b.id).orElseThrow();if("PENDING_PAYMENT".equals(fresh.status)){fresh.status="EXPIRED";bookings.save(fresh);}}}
}
@ResponseStatus(HttpStatus.CONFLICT) class BookingConflict extends RuntimeException {BookingConflict(String message){super(message);}}
@ResponseStatus(HttpStatus.NOT_FOUND) class BookingNotFound extends RuntimeException {BookingNotFound(String message){super(message);}}
