package com.arquisoft.seguridad.domain.auth.model;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.SeguridadMessages;

import java.util.List;

public record IdentidadToken(
        String identidadId,
        String correo,
        String nombre,
        List<String> roles) {

    public IdentidadToken {
        if (identidadId == null || identidadId.isBlank()) {
            throw new DomainException(SeguridadMessages.Identidad.ID_REQUERIDO,
                    SeguridadMessages.Identidad.IDENTIDAD_ID_REQUERIDO);
        }
        if (correo == null || correo.isBlank()) {
            throw new DomainException(SeguridadMessages.Identidad.CORREO_REQUERIDO,
                    SeguridadMessages.Identidad.IDENTIDAD_CORREO_REQUERIDO);
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
