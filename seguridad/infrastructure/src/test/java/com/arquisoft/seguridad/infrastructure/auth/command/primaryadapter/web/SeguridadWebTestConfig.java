package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Cadena permisiva compartida por los slices de los cuatro controllers de auth: lo que
// aquí se verifica es el enlace del body y la delegacion, no las reglas de acceso.
@TestConfiguration
@EnableWebSecurity
class SeguridadWebTestConfig {

    @Bean
    SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
