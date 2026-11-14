package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RegistroObservacionItemAsesorDomain;

public interface RegistrarObservacionItemAsesorValidator {

    void validar(
            RegistroObservacionItemAsesorDomain registro,
            ContextoEvaluacionCuantitativaAsesorDomain contexto,
            boolean descripcionYaExiste);
}
