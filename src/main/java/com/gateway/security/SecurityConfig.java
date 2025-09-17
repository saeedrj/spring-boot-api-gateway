package com.gateway.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${springdoc.api-docs.enabled}")
    private boolean isEnabledDoc;

    private final String[] reqMach = {"/auth/**"};
    private final String[] reqMachSwagger = {"/swagger/**", "/swagger-ui/**"};
    private final ObjectMapper objectMapper;


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       JwtAuthenticationProvider jwtAuthenticationProvider,
                                                       DaoAuthenticationProvider daoAuthenticationProvider
    ) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(jwtAuthenticationProvider)
                .authenticationProvider(daoAuthenticationProvider)
                .build();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {

        String[] allReqMach = !isEnabledDoc ? reqMach : Stream.concat(Arrays.stream(reqMach), Arrays.stream(reqMachSwagger))
                .toArray(String[]::new);

        http.csrf(AbstractHttpConfigurer::disable) //TODO enable in real
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allReqMach).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtRequestFilter(authenticationManager, objectMapper),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
