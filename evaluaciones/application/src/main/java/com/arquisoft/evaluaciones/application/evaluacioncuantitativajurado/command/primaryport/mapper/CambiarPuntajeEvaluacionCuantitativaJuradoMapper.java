package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;

public final class CambiarPuntajeEvaluacionCuantitativaJuradoMapper {

    private CambiarPuntajeEvaluacionCuantitativaJuradoMapper() {}

    public static CambioPuntajeEvaluacionCuantitativaJuradoDomain toDomain(
            CambiarPuntajeEvaluacionCuantitativaJuradoCommand command) {
        return CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                command.evaluacionCuantitativaJurado(), command.jurado(), command.nuevoPuntaje());
    }
}
