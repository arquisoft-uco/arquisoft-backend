package com.arquisoft.seguridad.infrastructure.config.keycloak;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.RolKey;
import com.arquisoft.shared.message.Mensajes;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class KeycloakJwtConverterConfig {

    private final AppLogger logger;

    private final KeycloakRolExtractor rolExtractor;

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::construirAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> construirAuthorities(Jwt jwt) {
        List<String> rolesRecurso = rolExtractor.extraerRolesRecurso(jwt);

        logger.debug(Mensajes.obtener(RolKey.LOG_ROLES_RECURSO), rolesRecurso);

        return rolesRecurso.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
