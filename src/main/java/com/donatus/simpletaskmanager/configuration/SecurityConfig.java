package com.donatus.simpletaskmanager.configuration;

import com.donatus.simpletaskmanager.security.JWTAuthenticationFilter;
import com.donatus.simpletaskmanager.security.JwtAuthEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthEntryPoint authEntryPoint;
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint) {
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(configure ->
                configure
                        .requestMatchers(antMatcher(HttpMethod.GET, "/health")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/user-mgmt/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/v1/user-mgmt/users")).hasAnyAuthority("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/task-mgmt/tasks")).hasAnyAuthority("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/task-mgmt/tasks_assign")).hasAnyAuthority("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/v1/task-mgmt/tasks")).hasAnyAuthority("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/v1/task-mgmt/assign")).hasAnyAuthority("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/v1/task-mgmt/tasks")).hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/v1/task-mgmt/tasks/users")).hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/v1/task-mgmt/tasks")).hasAnyAuthority("ADMIN", "USER")
                        .anyRequest().authenticated());
        http.httpBasic(HttpBasicConfigurer::disable);
        http.csrf(CsrfConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(){
        return new JWTAuthenticationFilter();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "localhost:5173"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
