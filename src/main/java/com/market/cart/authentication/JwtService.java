package com.market.cart.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {

    /// to get secret key do in git-bash: openssl rand -base64 32, or -base64 48
    /// this key: bndJi3wYaJHFFGbHcKxk5hoDGP5vKEXQpcER+rH+Svzzjt4+TmR/rtWIYItePgHa
    @Value("${app.security.secret-key}")
    private String secretKey;

    @Value("${app.security.jwt-expiration}")
    private long jwtExpiration;



    /// GENERATE TOKEN
    public String createToken(String username, String role) {

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
    /// Check if token is valid in terms of valid userDetails and expiration
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String subject = extractSubject(token);
        /// check if token subject equals authorized username. UserDetails draws from SecurityContextHolder
        return (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
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
