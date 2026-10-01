package com.arquisoft.seguridad.application.auth.command.secondaryport.model;

import java.util.List;

public record IdentidadProveedor(
        String identidadId,
        String correo,
        String nombre,
        List<String> roles) {
}
