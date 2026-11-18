package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RemocionObservacionItemJuradoDomain;

public interface RemoverObservacionItemJuradoValidator {

    void validar(
            RemocionObservacionItemJuradoDomain remocion,
            ObservacionItemJuradoDomain observacion,
            boolean evaluacionJuradoFinalizada);
}
