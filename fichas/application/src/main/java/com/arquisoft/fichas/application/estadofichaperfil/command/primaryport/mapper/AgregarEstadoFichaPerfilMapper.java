package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoFichaPerfilCommand;
import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;

public final class AgregarEstadoFichaPerfilMapper {

    private AgregarEstadoFichaPerfilMapper() {}

    public static AgregacionEstadoFichaPerfilDomain toDomain(AgregarEstadoFichaPerfilCommand command) {
        var estado = EstadoFichaPerfilDomain.crearPorAsesor(command.fichaPerfil(), command.estadoFicha());
        return AgregacionEstadoFichaPerfilDomain.crear(estado, command.asesorFicha());
    }
}
