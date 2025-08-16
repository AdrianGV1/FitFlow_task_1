package una.ac.cr.FitFlow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtProvider {

  private Key key() {
    String secret = System.getenv().getOrDefault(
        "JWT_SECRET",
        "dev_fallback_change_me_to_a_real_256bit_secret________________"
    );
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  private long validityMillis() {
    String raw = System.getenv().getOrDefault("JWT_VALIDITY_MILLIS", "21600000"); // 6h
    try { return Long.parseLong(raw); } catch (NumberFormatException e) { return 21600000L; }
  }

  public String generate(String subject, Collection<String> roles) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + validityMillis());
    return Jwts.builder()
      .setSubject(subject)
      .claim("roles", String.join(",", roles == null ? List.<String>of() : roles))
      .setIssuedAt(now)
      .setExpiration(exp)
      .signWith(key(), SignatureAlgorithm.HS512)
      .compact();
  }

  private Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
  }

  public boolean isValid(String token) {
    try { parse(token); return true; }
    catch (JwtException | IllegalArgumentException e) { return false; }
  }

  public String getSubject(String token) { return parse(token).getBody().getSubject(); }

  public List<String> getRoles(String token) {
    Object val = parse(token).getBody().get("roles");
    if (val == null) return List.of();
    String csv = String.valueOf(val);
    return csv.isBlank() ? List.of() : Arrays.asList(csv.split(","));
  }

  public long getExpirationEpoch(String token){
    return parse(token).getBody().getExpiration().toInstant().getEpochSecond();
  }
}
