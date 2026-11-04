package com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesAsignadasJuradoValidator {

    void validar(UUID jurado, boolean existe);
}
