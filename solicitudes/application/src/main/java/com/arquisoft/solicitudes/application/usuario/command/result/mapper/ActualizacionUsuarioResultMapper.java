package com.arquisoft.solicitudes.application.usuario.command.result.mapper;

import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.time.Instant;

public final class ActualizacionUsuarioResultMapper {

    private ActualizacionUsuarioResultMapper() {}

    public static ActualizacionUsuarioResult.Actualizada toResultActualizada(UsuarioDomain usuario) {
        return new ActualizacionUsuarioResult.Actualizada(usuario.getId());
    }

    public static ActualizacionUsuarioResult.Descartada toResultDescartada(
            UsuarioDomain usuario, Instant ocurridoEnVigente) {
        return new ActualizacionUsuarioResult.Descartada(usuario.getId(), ocurridoEnVigente);
    }

    public static ActualizacionUsuarioResult.NoReplicado toResultNoReplicado(UsuarioDomain usuario) {
        return new ActualizacionUsuarioResult.NoReplicado(usuario.getId());
    }
}
