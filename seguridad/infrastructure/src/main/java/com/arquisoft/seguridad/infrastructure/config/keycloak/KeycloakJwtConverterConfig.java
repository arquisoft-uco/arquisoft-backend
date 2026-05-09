package com.arquisoft.seguridad.infrastructure.config.keycloak;

import com.arquisoft.seguridad.domain.service.RoleAuthorityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Configura el bean JwtAuthenticationConverter orquestando:
 * - KeycloakRoleExtractor: extrae roles de realm_access.roles y resource_access.{clientId}.roles
 * - RoleAuthorityMapper: filtra y traduce roles de dominio (solo realm_access)
 * - Roles de resource_access: mapeo directo (permisos finos como ficha:ficha:view)
 * Sin prefijo ROLE_ — compatible con hasAuthority() en @PreAuthorize.
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
        List<String> resourceRoles = roleExtractor.extractResourceRoles(jwt);

        List<String> realmAuthorities = RoleAuthorityMapper.toAuthorityNames(realmRoles);

        List<String> resourceAuthorities = resourceRoles.stream()
                .toList();

        List<String> allAuthorityNames = new ArrayList<>();
        allAuthorityNames.addAll(realmAuthorities);
        allAuthorityNames.addAll(resourceAuthorities);

        log.debug("Realm roles: {} → {}, Resource roles: {} → {}",
                realmRoles, realmAuthorities, resourceRoles, resourceAuthorities);

        return allAuthorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
