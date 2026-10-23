package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesCualitativasJuradoRealizadasValidator {

    void validar(UUID evaluacionJurado, boolean existe, UUID juradoAsignado, UUID juradoSolicitante);
}
