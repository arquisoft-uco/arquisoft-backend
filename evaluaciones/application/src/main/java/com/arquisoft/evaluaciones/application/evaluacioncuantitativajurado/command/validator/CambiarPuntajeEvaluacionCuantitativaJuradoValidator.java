package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;

public interface CambiarPuntajeEvaluacionCuantitativaJuradoValidator {

    void validar(
            CambioPuntajeEvaluacionCuantitativaJuradoDomain cambio,
            EvaluacionCuantitativaJuradoDomain evaluacion,
            EstadoEvaluacionJuradoEntity estado,
            Integer valorMaximoItem);
}
