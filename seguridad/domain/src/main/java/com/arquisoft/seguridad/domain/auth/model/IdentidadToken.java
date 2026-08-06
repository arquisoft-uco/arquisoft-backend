package com.arquisoft.seguridad.domain.auth.model;

import com.arquisoft.shared.message.key.seguridad.IdentidadKey;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.DomainException;

import java.util.List;

public record IdentidadToken(
        String identidadId,
        String correo,
        String nombre,
        List<String> roles) {

    public IdentidadToken {
        if (identidadId == null || identidadId.isBlank()) {
            throw new DomainException(Messages.obtener(IdentidadKey.ERROR_ID_REQUERIDO),
                    SeguridadCodes.Identidad.IDENTIDAD_ID_REQUERIDO);
        }
        if (correo == null || correo.isBlank()) {
            throw new DomainException(Messages.obtener(IdentidadKey.ERROR_CORREO_REQUERIDO),
                    SeguridadCodes.Identidad.IDENTIDAD_CORREO_REQUERIDO);
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
