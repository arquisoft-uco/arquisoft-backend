package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.DescartarEvaluacionFichaCommand;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;

public final class DescartarEvaluacionFichaMapper {

    private DescartarEvaluacionFichaMapper() {}

    public static AgregacionEstadoEvaluacionFichaDomain toDomain(DescartarEvaluacionFichaCommand command) {
        var estadoEvaluacionFicha = EstadoEvaluacionFichaDomain.crearConEstado(
                command.evaluacionFichaPerfil(),
                EstadoEvaluacion.DESCARTADA.getId());

        return AgregacionEstadoEvaluacionFichaDomain.crear(
                estadoEvaluacionFicha,
                command.representanteComite());
    }
}
