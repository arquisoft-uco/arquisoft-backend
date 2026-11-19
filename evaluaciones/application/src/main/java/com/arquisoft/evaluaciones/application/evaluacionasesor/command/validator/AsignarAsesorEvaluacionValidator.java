package com.arquisoft.evaluaciones.application.evaluacionasesor.command.validator;

import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.CambioAsesorEvaluacion;

public interface AsignarAsesorEvaluacionValidator {

    void validar(
            ExistenciaEvaluacion evaluacion,
            ExistenciaAsesor asesor,
            EstadoAsignacionEvaluacion estado,
            CambioAsesorEvaluacion cambio);
}
