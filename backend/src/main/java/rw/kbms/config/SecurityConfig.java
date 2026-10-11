
package rw.kbms.config;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.*;

import org.springframework.web.cors.*;

import rw.kbms.model.Role;
import rw.kbms.model.UserAccount;
import rw.kbms.repo.UserRepository;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return email -> users.findByEmail(
                email.toLowerCase(Locale.ROOT)
        ).map(u ->
                new org.springframework.security.core.userdetails.User(
                        u.email,
                        u.passwordHash,
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_" + u.role.name()
                        ))
                )
        ).orElseThrow(() ->
                new UsernameNotFoundException(email)
        );
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource cors(
            @Value("${app.origin}") String origin) {

        CorsConfiguration c = new CorsConfiguration();

        c.setAllowedOrigins(List.of(origin));
        c.setAllowCredentials(true);
        c.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        c.setAllowedHeaders(
                List.of("Content-Type", "X-XSRF-TOKEN")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", c);
        return source;
    }

    @Bean
    SecurityFilterChain security(
            HttpSecurity http,
            @Value("${app.origin}") String origin,
            UserRepository users,
            PasswordEncoder passwords,
            Environment env) throws Exception {

        http
            .cors(corsSpec ->
                corsSpec.configurationSource(cors(origin))
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(
                    CookieCsrfTokenRepository.withHttpOnlyFalse()
                )
                .csrfTokenRequestHandler(
                    new CsrfTokenRequestAttributeHandler()
                )
            )
            .authorizeHttpRequests(a -> a
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/routes/**",
                    "/api/trips/**",
                    "/api/buses/**",
                    "/api/health",
                    "/api/auth/csrf"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/register",
                    "/api/auth/login"
                ).permitAll()

                .requestMatchers(
                    "/oauth2/**",
                    "/login/oauth2/**"
                ).permitAll()

                .requestMatchers(
                    "/api/admin/**",
                    "/api/bookings/admin/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/bookings/*/pay"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/buses/**",
                    "/api/routes/**",
                    "/api/trips/**"
                ).hasAnyRole("ADMIN", "OPERATOR")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/buses/**",
                    "/api/routes/**",
                    "/api/trips/**"
                ).hasAnyRole("ADMIN", "OPERATOR")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/buses/**",
                    "/api/routes/**",
                    "/api/trips/**"
                ).hasAnyRole("ADMIN", "OPERATOR")

                .anyRequest().authenticated()
            );

        if (Arrays.asList(env.getActiveProfiles()).contains("oauth")) {

            http.oauth2Login(oauth -> oauth.successHandler(
                (request, response, authentication) -> {

                    OAuth2User googleUser =
                            (OAuth2User) authentication.getPrincipal();

                    // Extract Google email safely
                    String email =
                            googleUser.getAttribute("email");

                    if (email == null || email.isBlank()) {
                        throw new IllegalStateException(
                            "Google did not provide an email address"
                        );
                    }

                    email = email.trim().toLowerCase(Locale.ROOT);

                    // Extract Google display name safely
                    String name = googleUser.getAttribute("name");

                    if (name == null || name.isBlank()) {
                        name = email;
                    }

                    // Create passenger account if not registered
                    if (users.findByEmail(email).isEmpty()) {

                        UserAccount newUser = new UserAccount(
                            name,
                            email,
                            passwords.encode(
                                UUID.randomUUID().toString()
                            ),
                            Role.PASSENGER
                        );

                        users.save(newUser);
                    }

                    UserAccount savedUser =
                            users.findByEmail(email).orElseThrow();

                    // Preserve the stored account role
                    var appAuthentication =
                            new UsernamePasswordAuthenticationToken(
                                savedUser.email,
                                null,
                                List.of(new SimpleGrantedAuthority(
                                    "ROLE_" + savedUser.role.name()
                                ))
                            );

                    // Save authentication into the session
                    SecurityContext context =
                            SecurityContextHolder.createEmptyContext();

                    context.setAuthentication(appAuthentication);
                    SecurityContextHolder.setContext(context);

                    HttpSessionSecurityContextRepository repository =
                            new HttpSessionSecurityContextRepository();

                    repository.saveContext(
                            context,
                            request,
                            response
                    );

                    response.sendRedirect(origin);
                }
            ));
        }

        http.logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                (request, response, authentication) ->
                    response.setStatus(204)
            )
        );

        http.exceptionHandling(e -> e
            .authenticationEntryPoint(
                (request, response, ex) ->
                    response.sendError(401)
            )
            .accessDeniedHandler(
                (request, response, ex) ->
                    response.sendError(403)
            )
        );

        return http.build();
    }
}
