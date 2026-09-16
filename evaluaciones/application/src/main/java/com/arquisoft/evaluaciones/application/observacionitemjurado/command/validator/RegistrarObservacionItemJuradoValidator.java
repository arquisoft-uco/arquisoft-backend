package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;

public interface RegistrarObservacionItemJuradoValidator {

    void validar(
            ObservacionItemJuradoDomain observacion,
            EvaluacionCuantitativaJuradoDomain evaluacion,
            EstadoEvaluacionJuradoEntity estado,
            boolean descripcionYaExiste);
}
