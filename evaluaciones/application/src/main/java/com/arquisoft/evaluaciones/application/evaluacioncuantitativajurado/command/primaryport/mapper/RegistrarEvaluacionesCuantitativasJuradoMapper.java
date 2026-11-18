package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.RegistroEvaluacionesCuantitativasJuradoDomain;

public final class RegistrarEvaluacionesCuantitativasJuradoMapper {

    private RegistrarEvaluacionesCuantitativasJuradoMapper() {}

    public static RegistroEvaluacionesCuantitativasJuradoDomain toDomain(
            RegistrarEvaluacionesCuantitativasJuradoCommand command) {
        var evaluaciones = command.evaluaciones().stream()
                .map(par -> EvaluacionCuantitativaJuradoDomain.crear(
                        command.evaluacionJurado(), par.item(), par.puntaje()))
                .toList();

        return RegistroEvaluacionesCuantitativasJuradoDomain.crear(
                command.evaluacionJurado(), command.jurado(), evaluaciones);
    }
}
