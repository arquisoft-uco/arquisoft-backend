package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;

public final class AgregarEstadoEvaluacionFichaMapper {

    private AgregarEstadoEvaluacionFichaMapper() {}

    public static AgregacionEstadoEvaluacionFichaDomain toDomain(AgregarEstadoEvaluacionFichaCommand command) {
        var estadoEvaluacionFicha = EstadoEvaluacionFichaDomain.crearConEstado(
                command.evaluacionFichaPerfil(),
                command.estadoEvaluacion());

        return AgregacionEstadoEvaluacionFichaDomain.crear(
                estadoEvaluacionFicha,
                command.representanteComite());
    }
}
