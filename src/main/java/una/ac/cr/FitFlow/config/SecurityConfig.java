package una.ac.cr.FitFlow.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import una.ac.cr.FitFlow.security.JwtFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor

public class SecurityConfig {

  
  private final JwtFilter jwtFilter;
  /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/graphql", "/graphiql")
                .permitAll()  // Deja accesible la API GraphQL y UI
                .anyRequest()
                .authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
*/
    
   
  @Bean
  SecurityFilterChain openChain(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .build();
  }
  
    @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}