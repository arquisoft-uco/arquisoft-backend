package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesCuantitativasJuradoRealizadasValidator {

    void validar(UUID evaluacionJurado, boolean existe, UUID juradoAsignado, UUID juradoSolicitante);
}
