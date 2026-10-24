package com.arquisoft.shared.web.identidad;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.UUID;

@TestConfiguration
public class IdentidadActualTestConfig implements WebMvcConfigurer {

    private static final String CLAIM_CORREO = "email";
    private static final String CLAIM_NOMBRE_USUARIO = "preferred_username";
    private static final String CLAIM_ROLES = "roles";

    @Bean
    ProveedorIdentidadActual proveedorIdentidadActualDePrueba() {
        return () -> {
            var autenticacion = SecurityContextHolder.getContext().getAuthentication();
            if (autenticacion == null || !autenticacion.isAuthenticated()) {
                throw new AuthenticationCredentialsNotFoundException("sin autenticacion");
            }
            var jwt = autenticacion.getPrincipal() instanceof Jwt token ? token : null;
            var id = parsear(autenticacion.getName());
            if (id == null) {
                throw new AuthenticationCredentialsNotFoundException("sub invalido");
            }
            return new IdentidadActual(
                    id,
                    jwt == null ? null : jwt.getClaimAsString(CLAIM_CORREO),
                    jwt == null ? null : jwt.getClaimAsString(CLAIM_NOMBRE_USUARIO),
                    jwt == null || jwt.getClaimAsStringList(CLAIM_ROLES) == null
                            ? List.of() : jwt.getClaimAsStringList(CLAIM_ROLES),
                    jwt == null ? null : jwt.getId(),
                    jwt == null ? null : jwt.getExpiresAt());
        };
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new IdentidadActualArgumentResolver(proveedorIdentidadActualDePrueba()));
    }

    private static UUID parsear(String texto) {
        try {
            return UUID.fromString(texto);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
