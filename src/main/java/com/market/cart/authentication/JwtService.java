package com.market.cart.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Service responsible for JSON Web Token (JWT) creation, validation,
 * and claim extraction.
 *
 * <p>
 * This service handles:
 * </p>
 * <ul>
 *     <li>JWT generation with custom claims</li>
 *     <li>JWT validation (subject & expiration)</li>
 *     <li>Claim extraction from tokens</li>
 * </ul>
 *
 * <p>
 * Also validates the existence of security secret key and if it is >32 characters long.
 * If not, shutdowns the application with {@link IllegalStateException}.
 * </p>
 *
 * <p>
 * Tokens are signed using HMAC SHA-256 with a Base64-encoded secret key.
 * </p>
 */
@Service
@Slf4j
public class JwtService {

    /// to get secret key do in git-bash: openssl rand -base64 32, or -base64 48
    /// app.security.secret-key=bndJi3wYaJHFFGbHcKxk5hoDGP5vKEXQpcER+rH+Svzzjt4+TmR/rtWIYItePgHa
    @Value("${app.security.secret-key}")
    private String secretKey;

    /// app.security.jwt-expiration=10800000
    @Value("${app.security.jwt-expiration}")
    private long jwtExpiration;

    /**
     * This method validates the existence of secret security key.
     * If the key is empty or < 32 characters long it throws an {@link IllegalStateException}
     * shutting down the application.
     */
    @PostConstruct
    public void validateSecretKey() {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";

        if (secretKey == null || secretKey.isBlank()) {
            log.error(ANSI_RED + "CRITICAL: 'app.security.secret-key' is missing or empty.\n" + ANSI_RESET);
            throw new IllegalStateException(ANSI_RED + "Application cannot start without a security secret key.\n" + ANSI_RESET);
        }

        if (secretKey.length() < 32) {
        log.error(ANSI_RED + "\n\nCRITICAL: 'app.security.secret-key' is too short for secure signatures.\n" + ANSI_RESET);
            throw new IllegalStateException(ANSI_RED + "Security key must be at least 32 characters long.\n" + ANSI_RESET);
        }

        log.info("Security secret key validated successfully.");
    }

    /**
     * Generates a signed JWT for the given user.
     *
     * @param username authenticated username
     * @param role     user role to be stored as a custom claim
     * @return signed JWT token
     */
    public String createToken(String username, String role) {
        log.info("Generating JWT token for user: {}", username);

        /// HashMap username and role into claims
        var claims = new HashMap<String, Object>();
        claims.put("role", role);
        return Jwts.builder()
                .setIssuer("self")
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))  //when token is created
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))    //when token expires
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT against user details and expiration.
     *
     * @param token       JWT token
     * @param userDetails authenticated user details
     * @return {@code true} if token is valid, otherwise {@code false}
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String subject = extractSubject(token);

        /// check if token subject equals authorized username. UserDetails draws from SecurityContextHolder
        boolean valid = subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
        log.info("Token validation result for user {}: {}", subject, valid);
        return valid;
    }

    /// EXTRACT TOKEN CLAIM
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /// EXTRACT ALL CLAIMS
    private Claims extractAllClaims(String token) {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }
    /// GET SIGN-IN KEY
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    /**
     * Extracts the username (subject) from a JWT contained in the request header.
     *
     * @param request HTTP servlet request
     * @return username stored in the token
     *
     * @throws StringIndexOutOfBoundsException if Authorization header is malformed
     */
    public String getUsernameFromToken(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or malformed Authorization header");
            return null;
        }

        String jwt = authHeader.substring(7).trim();
        return extractSubject(jwt);
     }

    /// EXTRACT SUBJECT
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /// TOKEN EXPIRED CHECK
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /// EXTRACT TOKEN EXPIRATION DATE
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
