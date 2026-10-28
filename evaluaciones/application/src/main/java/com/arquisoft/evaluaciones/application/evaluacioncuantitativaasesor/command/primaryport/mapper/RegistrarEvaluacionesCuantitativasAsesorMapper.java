package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.EvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.RegistroEvaluacionesCuantitativasAsesorDomain;

public final class RegistrarEvaluacionesCuantitativasAsesorMapper {

    private RegistrarEvaluacionesCuantitativasAsesorMapper() {}

    public static RegistroEvaluacionesCuantitativasAsesorDomain toDomain(
            RegistrarEvaluacionesCuantitativasAsesorCommand command) {
        var evaluaciones = command.evaluaciones().stream()
                .map(par -> EvaluacionCuantitativaAsesorDomain.crear(
                        command.evaluacionAsesor(), par.item(), par.puntaje()))
                .toList();

        return RegistroEvaluacionesCuantitativasAsesorDomain.crear(
                command.evaluacionAsesor(), command.asesor(), evaluaciones);
    }
}
