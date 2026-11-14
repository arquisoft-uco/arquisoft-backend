package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IdentidadAutenticada extends Authentication {

    String proveedor();

    String emisor();

    String sujeto();

    String correo();

    String nombreUsuario();

    List<String> roles();

    String idToken();

    Instant expiraEn();

    static Optional<IdentidadAutenticada> desde(Authentication autenticacion) {
        if (autenticacion instanceof IdentidadAutenticada identidad && identidad.isAuthenticated()) {
            return Optional.of(identidad);
        }
        return Optional.empty();
    }
}
