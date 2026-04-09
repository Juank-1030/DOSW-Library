package edu.eci.dosw.DOSW_Library.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) extractClaim(token, claims -> claims.get("roles", List.class));
        return roles;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // Extraer roles del UserDetails y agregarlos a los claims
        Map<String, Object> allClaims = new HashMap<>(extraClaims);
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        allClaims.put("roles", roles);

        Instant now = Instant.now();
        return Jwts.builder()
                .claims(allClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Retorna el tiempo de expiración del token en milisegundos.
     * 
     * <p>
     * <b>Configuración:</b> Via `security.jwt.expiration-ms` en
     * application.properties
     * </p>
     * 
     * <p>
     * <b>Valor por defecto:</b> 3600000 ms = 1 hora
     * </p>
     * 
     * @return Tiempo de expiración en ms
     */
    public Long getExpirationTime() {
        return jwtExpirationMs;
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date getExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
