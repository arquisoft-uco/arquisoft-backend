package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoOmisionEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionesCualitativasJurado;

public interface OmitirEvaluacionesCualitativasJuradoValidator {

    void validar(
            ExistenciaEvaluacionJurado evaluacionJurado,
            ExistenciaEvaluacionesCualitativasJurado evaluaciones,
            EstadoOmisionEvaluacionesCualitativasJurado estado);
}
