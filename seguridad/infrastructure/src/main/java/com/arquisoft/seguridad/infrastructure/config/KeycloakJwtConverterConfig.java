package com.arquisoft.seguridad.infrastructure.config;

import com.arquisoft.seguridad.domain.service.RoleAuthorityMapper;
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

/**
 * Configura el bean JwtAuthenticationConverter orquestando:
 * - KeycloakRoleExtractor: extrae roles del claim realm_access.roles
 * - RoleAuthorityMapper: filtra y traduce a ROLE_XXX del dominio
 */
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
        List<String> realmRoles = roleExtractor.extractRealmRoles(jwt);
        List<String> authorityNames = RoleAuthorityMapper.toAuthorityNames(realmRoles);

        log.debug("Roles del token: {} → authorities: {}", realmRoles, authorityNames);

        return authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
