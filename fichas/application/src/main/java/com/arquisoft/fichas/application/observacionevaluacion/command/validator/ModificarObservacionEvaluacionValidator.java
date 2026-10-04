package com.arquisoft.fichas.application.observacionevaluacion.command.validator;

import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;

public interface ModificarObservacionEvaluacionValidator {

    void validar(ModificacionObservacionEvaluacionDomain entrada, boolean observacionExiste,
                 PertenenciaObservacionEvaluacion pertenencia, boolean observacionYaExiste);
}
