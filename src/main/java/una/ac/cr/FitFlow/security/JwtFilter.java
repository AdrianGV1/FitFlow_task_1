package una.ac.cr.FitFlow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken; // 👈 nuevo
import org.springframework.security.core.Authentication;                     // 👈 nuevo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.validate(token)) { // firma/exp
            chain.doFilter(request, response);
            return;
        }

        // ✅ Reemplaza auth si es null, anónimo o no autenticado
        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        boolean shouldSetAuth = (existing == null)
                || (existing instanceof AnonymousAuthenticationToken)
                || (!existing.isAuthenticated());

        if (shouldSetAuth) {
            String email = jwtService.getUserNameFromToken(token);
            Set<String> roles = jwtService.getRolesFromToken(token);
            var authorities = (roles == null ? Set.<SimpleGrantedAuthority>of()
                    : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));

            var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
