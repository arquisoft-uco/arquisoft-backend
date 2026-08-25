package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;

import java.util.UUID;

public final class AsignarEstadoInicialFichaPerfilMapper {

    private AsignarEstadoInicialFichaPerfilMapper() {}

    public static EstadoFichaPerfilDomain toDomain(UUID fichaPerfil) {
        return EstadoFichaPerfilDomain.crear(fichaPerfil);
    }
}
