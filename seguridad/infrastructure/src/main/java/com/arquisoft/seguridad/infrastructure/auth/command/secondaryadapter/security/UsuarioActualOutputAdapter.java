package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.security;

import com.arquisoft.seguridad.application.auth.command.secondaryport.UsuarioActualOutputPort;
import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioActualOutputAdapter implements UsuarioActualOutputPort {

    // Claims estándar de OIDC: los fija el estándar y Keycloak los emite literalmente,
    // así que son contrato con el proveedor de identidad, no texto de catálogo.
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NOMBRE_PREFERIDO = "preferred_username";

    @Override
    public String obtenerIdUsuario() {
        var jwt = extraerJwt();
        return !UtilObjeto.esNulo(jwt) ? jwt.getSubject() : null;
    }

    @Override
    public String obtenerCorreo() {
        var jwt = extraerJwt();
        return !UtilObjeto.esNulo(jwt) ? jwt.getClaimAsString(CLAIM_EMAIL) : null;
    }

    @Override
    public String obtenerNombreUsuario() {
        var jwt = extraerJwt();
        if (UtilObjeto.esNulo(jwt)) {
            return null;
        }
        return UtilObjeto.aplicarPorDefecto(
                jwt.getClaimAsString(CLAIM_NOMBRE_PREFERIDO), jwt.getClaimAsString(CLAIM_EMAIL));
    }

    @Override
    public boolean tieneRol(String rol) {
        var auth = obtenerAutenticacion();
        if (UtilObjeto.esNulo(auth)) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol::equals);
    }

    @Override
    public List<String> obtenerRoles() {
        var auth = obtenerAutenticacion();
        if (UtilObjeto.esNulo(auth)) {
            return List.of();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    private Authentication obtenerAutenticacion() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Jwt extraerJwt() {
        var auth = obtenerAutenticacion();
        if (!UtilObjeto.esNulo(auth) && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }
}
