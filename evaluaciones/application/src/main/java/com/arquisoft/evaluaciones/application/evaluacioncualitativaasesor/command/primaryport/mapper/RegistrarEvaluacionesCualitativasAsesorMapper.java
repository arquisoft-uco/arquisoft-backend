package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.EvaluacionCualitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.RegistroEvaluacionesCualitativasAsesorDomain;

public final class RegistrarEvaluacionesCualitativasAsesorMapper {

    private RegistrarEvaluacionesCualitativasAsesorMapper() {}

    public static RegistroEvaluacionesCualitativasAsesorDomain toDomain(
            RegistrarEvaluacionesCualitativasAsesorCommand command) {
        var evaluaciones = command.evaluaciones().stream()
                .map(par -> EvaluacionCualitativaAsesorDomain.crear(
                        command.evaluacionAsesor(), par.item(), par.criterio()))
                .toList();

        return RegistroEvaluacionesCualitativasAsesorDomain.crear(
                command.evaluacionAsesor(), command.asesor(), evaluaciones);
    }
}
