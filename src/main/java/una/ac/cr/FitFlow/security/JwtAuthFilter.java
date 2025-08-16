package una.ac.cr.FitFlow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtProvider jwt;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String auth = req.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      if (jwt.isValid(token)) {
        String email = jwt.getSubject(token);
        List<SimpleGrantedAuthority> authorities = jwt.getRoles(token).stream()
          .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
          .map(SimpleGrantedAuthority::new)
          .toList();

        var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    chain.doFilter(req, res);
  }
}