package una.ac.cr.FitFlow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class JwtService {
    private final String secret;
    private final long validityMillis;

    public JwtService() {
        this.secret = System.getenv().getOrDefault("JWT_SECRET", "changeme");
        String expStr = System.getenv("JWT_ACCESS_EXPIRATION");
        long exp = (expStr != null && !expStr.isBlank()) ? Long.parseLong(expStr) : 900;
        this.validityMillis = exp * 1000;
    }

    public String generateToken(String email, Set<String> roles){
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMillis);
        return Jwts.builder()
            .setSubject(email)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
            .compact();
    }

    public String getUserNameFromToken(String token){
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public String getEmailFromToken(String token){
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Object rolesObj = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("roles");
        if (rolesObj instanceof java.util.Collection<?>) {
            return ((java.util.Collection<?>) rolesObj).stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet());
        }
        return Set.of();
    }

    public boolean validate(String token){
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationEpoch(String token){
        Date exp = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return exp.toInstant().getEpochSecond();
    }
}
