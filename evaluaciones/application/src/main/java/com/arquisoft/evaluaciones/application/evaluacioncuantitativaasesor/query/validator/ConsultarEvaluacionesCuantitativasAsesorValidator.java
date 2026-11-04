package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesCuantitativasAsesorValidator {

    void validar(UUID evaluacionAsesor, UUID asesorAsignado, UUID asesorSolicitante);
}
