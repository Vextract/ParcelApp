package com.mikhail.test.authdepartment.config.auth;

import com.mikhail.test.authdepartment.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests().requestMatchers(
                        "/api/auth/register/user",
                        "/api/auth/authenticate",
                        "/api/auth/register/admin",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui/index.html",
                        "/swagger-ui/**",
                        "/swagger-ui/index.html#/"
                        ).permitAll()
                //admin registration made open just for easier demonstration purposes
                .and()
                .authorizeHttpRequests().requestMatchers(
                        "/api/order/user/new",
                        "/api/order/user/edit/dest",
                        "/api/order/user/cancel/",
                        "/api/order/user/list/all"
                )
                .hasRole("USER")
                .and()
                .authorizeHttpRequests().requestMatchers(
                        "/api/auth/register/courier",
                        "/api/deliverers/admin/list",
                        "/api/order/manage/edit/status",
                        "/api/order/admin/assign",
                        "/api/order/admin/list/all",
                        "/api/order/admin/order/newdate"
                )
                .hasRole("ADMIN")
                .and()
                .authorizeHttpRequests().requestMatchers(
                        "/api/order/courier/list/all",
                        "/api/order/courier/order/"
                )
                .hasRole("COURIER")
                .and()
                .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthorizationTokenFilter(userService, jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
