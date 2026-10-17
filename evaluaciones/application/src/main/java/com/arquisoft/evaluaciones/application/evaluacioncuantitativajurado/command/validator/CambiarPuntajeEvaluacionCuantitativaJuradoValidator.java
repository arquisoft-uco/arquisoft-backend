package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;

public interface CambiarPuntajeEvaluacionCuantitativaJuradoValidator {

    void validar(
            CambioPuntajeEvaluacionCuantitativaJuradoDomain cambio,
            EvaluacionCuantitativaJuradoDomain evaluacion,
            EstadoEvaluacionJuradoDomain estado,
            ItemCuantitativoJuradoDomain item);
}
