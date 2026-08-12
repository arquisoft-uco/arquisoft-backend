package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.security;

import com.arquisoft.seguridad.application.auth.command.secondaryport.UsuarioActualOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioActualOutputAdapter implements UsuarioActualOutputPort {

    @Override
    public String obtenerIdUsuario() {
        Jwt jwt = extraerJwt();
        return jwt != null ? jwt.getSubject() : null;
    }

    @Override
    public String obtenerCorreo() {
        Jwt jwt = extraerJwt();
        return jwt != null ? jwt.getClaimAsString("email") : null;
    }

    @Override
    public String obtenerNombreUsuario() {
        Jwt jwt = extraerJwt();
        if (jwt == null) {
            return null;
        }
        String nombrePreferido = jwt.getClaimAsString("preferred_username");
        return nombrePreferido != null ? nombrePreferido : jwt.getClaimAsString("email");
    }

    @Override
    public boolean tieneRol(String rol) {
        Authentication auth = obtenerAutenticacion();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol::equals);
    }

    @Override
    public List<String> obtenerRoles() {
        Authentication auth = obtenerAutenticacion();
        if (auth == null) {
            return Collections.emptyList();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private Authentication obtenerAutenticacion() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Jwt extraerJwt() {
        Authentication auth = obtenerAutenticacion();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }
}
