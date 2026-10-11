package com.arquisoft.fichas.application.observacionevaluacion.command.validator;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;

public interface AgregarObservacionEvaluacionValidator {

    void validar(AgregacionObservacionEvaluacionDomain entrada, boolean evaluacionExiste, boolean esPropietario,
                 EstadoEvaluacion ultimoEstado, boolean observacionYaExiste);
}
