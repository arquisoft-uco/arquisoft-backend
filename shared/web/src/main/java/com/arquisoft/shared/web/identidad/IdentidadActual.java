package com.arquisoft.shared.web.identidad;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IdentidadActual(UUID id, String correo, String nombreUsuario, List<String> roles, String idToken,
                              Instant expiraEn) {

    public IdentidadActual {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}
