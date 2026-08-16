package com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;

public final class RegistrarEvaluacionFichaPerfilMapper {

    private RegistrarEvaluacionFichaPerfilMapper() {}

    public static EvaluacionFichaPerfilDomain toDomain(RegistrarEvaluacionFichaPerfilCommand command) {
        return EvaluacionFichaPerfilDomain.crear(command.representanteComite(), command.fichaPerfil());
    }
}
