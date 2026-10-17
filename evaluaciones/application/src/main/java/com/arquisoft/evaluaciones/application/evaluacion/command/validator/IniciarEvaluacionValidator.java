package com.arquisoft.evaluaciones.application.evaluacion.command.validator;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;

public interface IniciarEvaluacionValidator {

    void validar(EstadoEvaluacion estadoActual);
}
