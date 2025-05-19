package Daniel24356.com.Ecommerce.security.config;

import Daniel24356.com.Ecommerce.exceptions.RestAccessDeniedHandler;
import Daniel24356.com.Ecommerce.exceptions.RestAuthenticationEntryPoint;
import Daniel24356.com.Ecommerce.exceptions.RestAuthenticationFailureHandler;
import Daniel24356.com.Ecommerce.security.jwt.JwtAuthenticationFilter;
import Daniel24356.com.Ecommerce.security.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import Daniel24356.com.Ecommerce.security.jwt.JwtAuthorizationFilter;
import Daniel24356.com.Ecommerce.security.jwt.JwtTokenProvider;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(authManager, jwtTokenProvider);
        authFilter.setFilterProcessesUrl("/api/v1/auth/login");
        authFilter.setAuthenticationFailureHandler(failureHandler);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/profile-picture").authenticated()
                        // all other calls require MANAGE_USERS
                        .anyRequest().hasAuthority("MANAGE_USERS")
                )
                .addFilter(authFilter)
                .addFilterBefore(
                        new JwtAuthorizationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}