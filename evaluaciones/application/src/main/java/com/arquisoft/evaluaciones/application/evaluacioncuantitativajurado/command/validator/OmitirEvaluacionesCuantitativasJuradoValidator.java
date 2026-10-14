package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoOmisionEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ObservacionesEvaluacionesCuantitativasJurado;

public interface OmitirEvaluacionesCuantitativasJuradoValidator {

    void validar(
            ExistenciaEvaluacionJurado evaluacionJurado,
            ExistenciaEvaluacionesCuantitativasJurado evaluaciones,
            EstadoOmisionEvaluacionesCuantitativasJurado estado,
            ObservacionesEvaluacionesCuantitativasJurado observaciones);
}
