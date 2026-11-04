package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;

public interface ModificarObservacionItemJuradoValidator {

    void validar(
            ModificacionObservacionItemJuradoDomain modificacion,
            ObservacionItemJuradoDomain observacion,
            boolean evaluacionJuradoFinalizada,
            boolean descripcionEnOtraObservacion);
}
