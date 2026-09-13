package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator;

import java.util.UUID;

public interface ConsultarEvaluacionesCuantitativasJuradoValidator {

    void validar(UUID evaluacionJurado, boolean existe, boolean pertenece);
}
