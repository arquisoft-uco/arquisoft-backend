package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesCualitativasJuradoValidator {

    void validar(UUID evaluacionJurado, boolean existe, boolean pertenece);
}
