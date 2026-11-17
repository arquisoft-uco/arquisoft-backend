package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.CambioPuntajeEvaluacionCuantitativaAsesorDomain;

public final class CambiarPuntajeEvaluacionCuantitativaAsesorMapper {

    private CambiarPuntajeEvaluacionCuantitativaAsesorMapper() {}

    public static CambioPuntajeEvaluacionCuantitativaAsesorDomain toDomain(
            CambiarPuntajeEvaluacionCuantitativaAsesorCommand command) {
        return CambioPuntajeEvaluacionCuantitativaAsesorDomain.crear(
                command.evaluacionCuantitativaAsesor(), command.asesor(), command.nuevoPuntaje());
    }
}
