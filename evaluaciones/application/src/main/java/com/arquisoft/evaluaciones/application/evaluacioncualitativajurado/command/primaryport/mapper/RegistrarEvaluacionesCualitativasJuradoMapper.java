package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.RegistrarEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.RegistroEvaluacionesCualitativasJuradoDomain;

public final class RegistrarEvaluacionesCualitativasJuradoMapper {

    private RegistrarEvaluacionesCualitativasJuradoMapper() {}

    public static RegistroEvaluacionesCualitativasJuradoDomain toDomain(
            RegistrarEvaluacionesCualitativasJuradoCommand command) {
        var evaluaciones = command.evaluaciones().stream()
                .map(par -> EvaluacionCualitativaJuradoDomain.crear(
                        command.evaluacionJurado(), par.item(), par.criterio()))
                .toList();

        return RegistroEvaluacionesCualitativasJuradoDomain.crear(evaluaciones);
    }
}
