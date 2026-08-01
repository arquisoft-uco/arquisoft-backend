package com.arquisoft.seguridad.infrastructure.config.keycloak;

import com.arquisoft.shared.message.SeguridadMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeycloakJwtConverterConfig {

    private final KeycloakRoleExtractor roleExtractor;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::buildAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> buildAuthorities(Jwt jwt) {
        List<String> resourceRoles = roleExtractor.extractResourceRoles(jwt);

        log.debug(SeguridadMessages.Rol.LOG_RESOURCE_ROLES, resourceRoles);

        return resourceRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
