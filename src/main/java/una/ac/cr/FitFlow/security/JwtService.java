package una.ac.cr.FitFlow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {
    private static final String DEFAULT_SECRET_BASE64 = "9kkZxBS2H4F3s4xPomzPeJICq61NP1BXicImDxloBdallDph84ggdZmopTOa9uSQ3w8lhpjAPa7pb/Sb8llEVA==";

    private final long validityMillis;
    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtService() {
        String rawSecret = System.getenv().getOrDefault("JWT_SECRET", DEFAULT_SECRET_BASE64);
        byte[] secretBytes = decodeSecret(rawSecret);
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.jwtParser = Jwts.parserBuilder()
            .setSigningKey(this.signingKey)
            .build();
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
            .signWith(signingKey, SignatureAlgorithm.HS512)
            .compact();
    }

    public String getUserNameFromToken(String token){
        return jwtParser.parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public String getEmailFromToken(String token){
        return jwtParser.parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Object rolesObj = jwtParser.parseClaimsJws(token)
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
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationEpoch(String token){
        Date exp = jwtParser.parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return exp.toInstant().getEpochSecond();
    }

    public long getValidityMillis() {
        return validityMillis;
    }

    private byte[] decodeSecret(String secretValue) {
        String trimmed = secretValue == null ? "" : secretValue.trim();
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(trimmed);
        } catch (IllegalArgumentException ex) {
            decoded = trimmed.getBytes(StandardCharsets.UTF_8);
        }

        if (decoded.length < 64) {
            throw new IllegalArgumentException(
                "JWT secret must be at least 64 bytes when using HS512. Current length: " + decoded.length);
        }

        return decoded;
    }

}
