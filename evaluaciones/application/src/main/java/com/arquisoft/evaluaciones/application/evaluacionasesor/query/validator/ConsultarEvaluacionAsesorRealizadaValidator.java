package com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionAsesorRealizadaValidator {

    void validar(UUID evaluacionAsesor, UUID asesorAsignado, UUID asesorSolicitante);
}
