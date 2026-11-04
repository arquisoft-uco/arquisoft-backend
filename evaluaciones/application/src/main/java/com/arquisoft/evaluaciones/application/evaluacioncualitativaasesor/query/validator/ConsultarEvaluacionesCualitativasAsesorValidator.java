package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesCualitativasAsesorValidator {

    void validar(UUID evaluacionAsesor, UUID asesorAsignado, UUID asesorSolicitante);
}
