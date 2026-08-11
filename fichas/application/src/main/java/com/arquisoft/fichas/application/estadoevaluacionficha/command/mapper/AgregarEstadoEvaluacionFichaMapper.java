package com.arquisoft.fichas.application.estadoevaluacionficha.command.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;

public final class AgregarEstadoEvaluacionFichaMapper {

    private AgregarEstadoEvaluacionFichaMapper() {}

    public static AgregacionEstadoEvaluacionFichaDomain toDomain(AgregarEstadoEvaluacionFichaCommand command) {
        return AgregacionEstadoEvaluacionFichaDomain.crear(
                command.evaluacionFichaPerfil(),
                command.estadoEvaluacion(),
                command.representanteComite());
    }
}
