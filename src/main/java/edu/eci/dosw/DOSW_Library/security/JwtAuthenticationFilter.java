package edu.eci.dosw.DOSW_Library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.io.IOException;

/**
 * Filtro JWT para autenticación sin estado (STATELESS).
 * 
 * <p>
 * <b>Responsabilidad:</b> Interceptar cada request HTTP entrante, extraer el
 * token JWT del header
 * "Authorization", validar su autenticidad y cargar la información del usuario
 * en el contexto de
 * seguridad de Spring.
 * </p>
 * 
 * <p>
 * <b>Ubicación en la cadena de filtros:</b> Se ejecuta ANTES que
 * {@link UsernamePasswordAuthenticationFilter}
 * (ver {@link SecurityConfig#securityFilterChain}).
 * </p>
 * 
 * <p>
 * <b>Herencia:</b> Extiende {@link OncePerRequestFilter} para garantizar que se
 * ejecuta exactamente
 * una vez por request (incluso si hay forward o include internos).
 * </p>
 * 
 * <p>
 * <b>Flujo de procesamiento:</b>
 * </p>
 * <ol>
 * <li>Intercepta request (automático via Spring Security)</li>
 * <li>Lee header "Authorization"</li>
 * <li>Valida formato "Bearer &lt;token&gt;"</li>
 * <li>Extrae el JWT (token)</li>
 * <li>Valida firma y expiración</li>
 * <li>Carga UserDetails desde BD</li>
 * <li>Crea UsernamePasswordAuthenticationToken con roles</li>
 * <li>Carga en SecurityContextHolder</li>
 * <li>Continúa con siguiente filtro</li>
 * </ol>
 * 
 * <p>
 * <b>Casos especiales:</b>
 * </p>
 * <ul>
 * <li><b>Sin token:</b> Continúa sin autenticación (permitirá si endpoint es
 * público)</li>
 * <li><b>Token inválido:</b> Se loguea, se continúa sin autenticación</li>
 * <li><b>Token expirado:</b> Se rechaza en próxima etapa
 * (GlobalExceptionHandler)</li>
 * <li><b>Usuario no existe:</b> Se captura UsernameNotFoundException</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con logging y manejo mejorado de excepciones
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param jwtService         Servicio para generar y validar JWT
     * @param userDetailsService Servicio para cargar detalles del usuario
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        logger.debug("JwtAuthenticationFilter initialized");
    }

    /**
     * Filtra cada request HTTP: extrae, valida y carga el JWT en el contexto de
     * seguridad.
     * 
     * <p>
     * <b>Paso 1: Verificar presencia del header Authorization</b>
     * </p>
     * 
     * <pre>
     * - Si no existe o no comienza con "Bearer " → continuar sin autenticación
     * - Esto permite requests públicos (ej: /auth/login, /swagger-ui/html)
     * </pre>
     * 
     * <p>
     * <b>Paso 2: Extraer token del header</b>
     * </p>
     * 
     * <pre>
     * - Formato esperado: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
     * - Se toma desde posición 7 (salta "Bearer " = 7 caracteres)
     * </pre>
     * 
     * <p>
     * <b>Paso 3: Extraer username del JWT</b>
     * </p>
     * 
     * <pre>
     * - Llama a JwtService.extractUsername(jwt)
     * - Si falla (token corrupto) → capturar excepción y continuar
     * </pre>
     * 
     * <p>
     * <b>Paso 4: Cargar UserDetails desde BD</b>
     * </p>
     * 
     * <pre>
     * - Busca usuario por username en InMemoryUserDetailsManager
     * - Si no existe → UsernameNotFoundException
     * </pre>
     * 
     * <p>
     * <b>Paso 5: Validar integridad y expiración del JWT</b>
     * </p>
     * 
     * <pre>
     * - Llama a JwtService.isTokenValid(jwt, userDetails)
     * - Verifica:
     *   1. Firma HMAC-SHA (no fue modificado)
     *   2. Username en JWT coincide con UserDetails
     *   3. Token no está expirado (fecha actual < exp claim)
     * </pre>
     * 
     * <p>
     * <b>Paso 6: Cargar en SecurityContext con roles</b>
     * </p>
     * 
     * <pre>
     * - Crea UsernamePasswordAuthenticationToken
     * - Incluye UserDetails con autoridades (roles) del usuario
     * - SecurityContext cargado → disponible para @PreAuthorize en endpoints
     * </pre>
     * 
     * @param request     Solicitud HTTP entrante
     * @param response    Respuesta HTTP (no modificada por este filtro)
     * @param filterChain Cadena de filtros para continuar
     * @throws ServletException Si hay error en servlet
     * @throws IOException      Si hay error de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        // ============================================
        // PASO 1: Verificar presencia del header Authorization
        // ============================================

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug(
                    "No Bearer token found in Authorization header - request will be processed without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        // ============================================
        // PASO 2: Extraer token del header
        // ============================================

        String jwt = authHeader.substring(7); // Skip "Bearer " (7 chars)
        logger.debug("Bearer token extracted, length: {}", jwt.length());

        // ============================================
        // PASO 3: Extraer username del JWT
        // ============================================

        String username;
        try {
            username = jwtService.extractUsername(jwt);
            logger.debug("Username extracted from JWT: {}", username);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired");
            filterChain.doFilter(request, response);
            return;
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token format");
            filterChain.doFilter(request, response);
            return;
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token is not supported");
            filterChain.doFilter(request, response);
            return;
        } catch (Exception ex) {
            logger.warn("Error extracting username from JWT: {}", ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // ============================================
        // PASO 4: Verificar si ya existe autenticación
        // ============================================

        // Si ya está autenticado (por otro mecanismo), no procesar de nuevo
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ============================================
            // PASO 5: Cargar UserDetails desde BD
            // ============================================

            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
                logger.debug("UserDetails loaded successfully for user: {} | Authorities: {}",
                        username, userDetails.getAuthorities());
            } catch (UsernameNotFoundException e) {
                logger.warn("User not found in database: {}", username);
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                logger.error("Error loading UserDetails for user: {}", username, e);
                filterChain.doFilter(request, response);
                return;
            }

            // ============================================
            // PASO 6: Validar integridad y expiración del JWT
            // ============================================

            if (jwtService.isTokenValid(jwt, userDetails)) {
                logger.debug("JWT token is VALID for user: {} - creating authentication", username);

                // ============================================
                // PASO 7: Crear token de autenticación
                // ============================================

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()); // ← Roles incluidos

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ============================================
                // PASO 8: Cargar en SecurityContext
                // ============================================

                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Authentication successful for user: {} | IP: {} | Endpoint: {}",
                        username, request.getRemoteAddr(), request.getRequestURI());

            } else {
                logger.warn("JWT token is INVALID for user: {} - signature or expiration failed", username);
            }
        }

        // ============================================
        // PASO 9: Continuar con siguiente filtro
        // ============================================

        filterChain.doFilter(request, response);
    }
}
