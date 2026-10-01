package com.arquisoft.solicitudes.application.usuario.command.result.mapper;

import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.time.Instant;

public final class AgregacionUsuarioResultMapper {

    private AgregacionUsuarioResultMapper() {}

    public static AgregacionUsuarioResult.Agregada toResultAgregada(UsuarioDomain usuario) {
        return new AgregacionUsuarioResult.Agregada(usuario.getId());
    }

    public static AgregacionUsuarioResult.Duplicada toResultDuplicada(UsuarioDomain usuario) {
        return new AgregacionUsuarioResult.Duplicada(usuario.getId());
    }

    public static AgregacionUsuarioResult.Descartada toResultDescartada(
            UsuarioDomain usuario, Instant ocurridoEnVigente) {
        return new AgregacionUsuarioResult.Descartada(usuario.getId(), ocurridoEnVigente);
    }
}
