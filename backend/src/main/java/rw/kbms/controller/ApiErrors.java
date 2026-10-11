package rw.kbms.controller;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Map;
@RestControllerAdvice public class ApiErrors {
 @ExceptionHandler({BookingConflict.class}) ResponseEntity<?> bookingConflict(BookingConflict e){return ResponseEntity.status(409).body(Map.of("error",e.getMessage()));}
 @ExceptionHandler({BookingNotFound.class}) ResponseEntity<?> bookingNotFound(BookingNotFound e){return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));}
 @ExceptionHandler({IllegalArgumentException.class}) ResponseEntity<?> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
 @ExceptionHandler({DataIntegrityViolationException.class}) ResponseEntity<?> conflict(DataIntegrityViolationException e){return ResponseEntity.status(409).body(Map.of("error","Record conflicts with an existing record or constraint"));}
 @ExceptionHandler({MethodArgumentNotValidException.class}) ResponseEntity<?> validation(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("error",e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));}
 @ExceptionHandler({org.springframework.security.authentication.BadCredentialsException.class}) ResponseEntity<?> badLogin(Exception e){return ResponseEntity.status(401).body(Map.of("error","Invalid email or password"));}
}
