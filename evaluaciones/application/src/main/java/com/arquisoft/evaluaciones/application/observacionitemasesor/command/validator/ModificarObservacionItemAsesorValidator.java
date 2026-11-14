package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;

public interface ModificarObservacionItemAsesorValidator {

    void validar(
            ModificacionObservacionItemAsesorDomain modificacion,
            ContextoEvaluacionCuantitativaAsesorDomain contexto,
            boolean descripcionEnOtraObservacion);
}
