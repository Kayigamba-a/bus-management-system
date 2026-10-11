package rw.kbms.controller;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import rw.kbms.repo.UserRepository;
import rw.kbms.model.*;
import java.util.*;
@RestController @RequestMapping("/api/auth") public class AuthController {
 private final UserRepository users; private final PasswordEncoder password;private final AuthenticationManager manager;
 public AuthController(UserRepository users,PasswordEncoder password,AuthenticationManager manager){this.users=users;this.password=password;this.manager=manager;}
 public record Register(@NotBlank String name,@Email @NotBlank String email,@Size(min=8) String password){}
 public record Login(String email,String password){}
 @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken token){return Map.of("token",token.getToken());}
 @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody Register req){String email=req.email().trim().toLowerCase();if(users.findByEmail(email).isPresent())return ResponseEntity.status(409).body(Map.of("error","Email already registered"));return ResponseEntity.status(201).body(users.save(new UserAccount(req.name(),email,password.encode(req.password()),Role.PASSENGER)));}
 @PostMapping("/login") public Object login(@RequestBody Login req,HttpServletRequest request,HttpServletResponse response){var a=manager.authenticate(new UsernamePasswordAuthenticationToken(req.email().toLowerCase(),req.password()));var ctx=SecurityContextHolder.createEmptyContext();ctx.setAuthentication(a);SecurityContextHolder.setContext(ctx);new HttpSessionSecurityContextRepository().saveContext(ctx,request,response);return users.findByEmail(a.getName()).orElseThrow();}
 @GetMapping("/me") public UserAccount me(org.springframework.security.core.Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
}
