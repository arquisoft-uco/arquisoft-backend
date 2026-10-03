package com.arquisoft.fichas.application.observacionevaluacion.command.validator;

import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;

public interface RemoverObservacionEvaluacionValidator {

    void validar(RemocionObservacionEvaluacionDomain entrada, boolean observacionExiste,
                 PertenenciaObservacionEvaluacion pertenencia);
}
