package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.CambioCriterioEvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;

import java.util.Set;
import java.util.UUID;

public interface CambiarCriterioEvaluacionCualitativaJuradoValidator {

    void validar(
            CambioCriterioEvaluacionCualitativaJuradoDomain cambio,
            EvaluacionCualitativaJuradoDomain evaluacion,
            EstadoEvaluacionJuradoDomain estado,
            Set<UUID> criteriosExistentes);
}
