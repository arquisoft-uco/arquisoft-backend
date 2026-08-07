package com.arquisoft.seguridad.infrastructure.config.keycloak;

import com.arquisoft.shared.message.key.seguridad.RolKey;
import com.arquisoft.shared.message.CatalogoMensajes;
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

    private final KeycloakRolExtractor rolExtractor;
    private final CatalogoMensajes catalogo;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::construirAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> construirAuthorities(Jwt jwt) {
        List<String> rolesRecurso = rolExtractor.extraerRolesRecurso(jwt);

        log.debug(catalogo.obtener(RolKey.LOG_ROLES_RECURSO), rolesRecurso);

        return rolesRecurso.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
