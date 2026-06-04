package com.arquisoft.seguridad.domain.auth.model;

import com.arquisoft.shared.exception.DomainException;

import java.util.List;

public record IdentidadToken(
        String identidadId,
        String correo,
        String nombre,
        List<String> roles) {

    public IdentidadToken {
        if (identidadId == null || identidadId.isBlank()) {
            throw new DomainException("El identificador de identidad no puede ser nulo ni vacio",
                    "IDENTIDAD_ID_REQUERIDO");
        }
        if (correo == null || correo.isBlank()) {
            throw new DomainException("El correo no puede ser nulo ni vacio",
                    "IDENTIDAD_CORREO_REQUERIDO");
        }
        roles = roles != null ? List.copyOf(roles) : List.of();
    }

    public static IdentidadToken de(
            String identidadId,
            String correo,
            String nombre,
            List<String> roles) {
        return new IdentidadToken(identidadId, correo, nombre, roles);
    }
}
