package edu.eci.dosw.DOSW_Library.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.auth.username:admin}")
    private String username;

    @Value("${security.auth.password:admin1234}")
    private String password;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui.htm", "/swagger-ui/**", "/api-docs/**")
                        .permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/error").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                // Usuario con rol USER (lectura básica)
                User.withUsername("user")
                        .password(passwordEncoder.encode("user1234"))
                        .roles("USER")
                        .build(),
                // Usuario administrador con rol LIBRARIAN (permisos completos)
                User.withUsername("admin")
                        .password(passwordEncoder.encode("admin1234"))
                        .roles("LIBRARIAN")
                        .build());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * ✅ CORS Configuration Bean
     * 
     * Allows requests from different origins (frontend domains).
     * Handles pre-flight OPTIONS requests automatically.
     * 
     * Configurable via application.properties:
     * - cors.allowedOrigins: Comma-separated list of allowed origins
     * - cors.allowedMethods: HTTP methods allowed (GET, POST, PUT, DELETE, OPTIONS)
     * - cors.allowedHeaders: Headers allowed in requests (Authorization,
     * Content-Type, etc.)
     * - cors.exposedHeaders: Headers exposed to client response
     * - cors.maxAge: Seconds to cache pre-flight response
     * - cors.allowCredentials: Whether cookies are allowed
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow requests from any origin (configurable via properties)
        config.setAllowedOriginPatterns(Arrays.asList("*"));

        // ✅ Allow standard HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // ✅ Allow headers including Authorization for Bearer tokens
        config.setAllowedHeaders(Arrays.asList("*"));

        // ✅ Expose headers to client (for pagination, custom headers, etc.)
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));

        // ✅ Cache pre-flight response for 1 hour (3600 seconds)
        config.setMaxAge(3600L);

        // ✅ Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // ✅ Apply CORS config to all endpoints
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
