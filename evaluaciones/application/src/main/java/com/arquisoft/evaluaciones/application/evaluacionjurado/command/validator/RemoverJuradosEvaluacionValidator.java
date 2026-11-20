package com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosEvaluadosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosAsignadosEvaluacion;

public interface RemoverJuradosEvaluacionValidator {

    void validar(
            ExistenciaEvaluacion evaluacion,
            EstadoAsignacionEvaluacion estado,
            JuradosAsignadosEvaluacion jurados,
            JuradosEvaluadosEvaluacion evaluados);
}
