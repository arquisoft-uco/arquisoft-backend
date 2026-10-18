package com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesJuradoValidator {

    void validar(UUID evaluacion, boolean existe);
}
