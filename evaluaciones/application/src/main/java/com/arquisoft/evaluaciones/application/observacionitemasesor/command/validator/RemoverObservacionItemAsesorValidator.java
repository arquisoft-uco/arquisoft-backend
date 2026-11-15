package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RemocionObservacionItemAsesorDomain;

public interface RemoverObservacionItemAsesorValidator {

    void validar(
            RemocionObservacionItemAsesorDomain remocion,
            ContextoEvaluacionCuantitativaAsesorDomain contexto);
}
