package com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionAsesorValidator {

    void validar(UUID evaluacion, boolean evaluacionExiste, UUID evaluacionAsesor);
}
