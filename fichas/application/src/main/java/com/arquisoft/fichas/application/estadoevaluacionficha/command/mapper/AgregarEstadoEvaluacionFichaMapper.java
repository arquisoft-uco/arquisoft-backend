package com.arquisoft.fichas.application.estadoevaluacionficha.command.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregarEstadoEvaluacionFichaDomain;

public final class AgregarEstadoEvaluacionFichaMapper {

    private AgregarEstadoEvaluacionFichaMapper() {}

    public static AgregarEstadoEvaluacionFichaDomain toDomain(AgregarEstadoEvaluacionFichaCommand command) {
        return AgregarEstadoEvaluacionFichaDomain.crear(
                command.evaluacionFichaPerfil(),
                command.estadoEvaluacion(),
                command.representanteComite());
    }
}
