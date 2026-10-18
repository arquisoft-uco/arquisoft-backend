package com.arquisoft.evaluaciones.application.observacionitemjurado.query.validator;

import java.util.UUID;

public interface ConsultarObservacionesItemJuradoValidator {

    void validar(UUID evaluacionCuantitativaJurado, boolean existe);
}
