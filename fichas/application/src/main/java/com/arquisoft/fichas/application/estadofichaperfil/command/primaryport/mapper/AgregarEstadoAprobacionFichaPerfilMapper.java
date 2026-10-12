package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoAprobacionFichaPerfilCommand;
import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;

public final class AgregarEstadoAprobacionFichaPerfilMapper {

    private AgregarEstadoAprobacionFichaPerfilMapper() {}

    public static DecisionFichaPerfilDomain toDomain(AgregarEstadoAprobacionFichaPerfilCommand command) {
        return DecisionFichaPerfilDomain.crear(command.fichaPerfil(), command.acepta(), command.coordinador());
    }
}
