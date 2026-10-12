package com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator;

import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.DisponibilidadJuradosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.ExistenciaJurados;

public interface AsignarJuradosEvaluacionValidator {

    void validar(
            ExistenciaEvaluacion evaluacion,
            EstadoRegistroEvaluacion estado,
            ExistenciaJurados jurados,
            DisponibilidadJuradosEvaluacion disponibilidad);
}
