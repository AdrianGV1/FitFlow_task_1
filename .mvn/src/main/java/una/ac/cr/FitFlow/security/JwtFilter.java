
package una.ac.cr.FitFlow.security;

import una.ac.cr.FitFlow.model.User;
import una.ac.cr.FitFlow.repository.AuthTokenRepository;
import una.ac.cr.FitFlow.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepo;
  private final AuthTokenRepository tokenRepo;

  public JwtFilter(JwtService jwtService, UserRepository userRepo, AuthTokenRepository tokenRepo) {
    this.jwtService = jwtService; this.userRepo = userRepo; this.tokenRepo = tokenRepo;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    userEmail = jwtService.getUserNameFromToken(jwt);
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      User user = userRepo.findByEmail(userEmail).orElse(null);
      var isTokenValid = tokenRepo.findByToken(jwt)
          .map(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
          .orElse(false);
      if (user != null && isTokenValid) {
        var authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getModule() + "_" + role.getPermission()))
            .collect(Collectors.toSet());
        var authToken = new UsernamePasswordAuthenticationToken(
            user,
            null,
            authorities
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    chain.doFilter(request, response);
  }
}