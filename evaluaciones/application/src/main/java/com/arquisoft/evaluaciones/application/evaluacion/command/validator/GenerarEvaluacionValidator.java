package com.arquisoft.evaluaciones.application.evaluacion.command.validator;

import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;

public interface GenerarEvaluacionValidator {

    void validar(ExistenciaAsesor asesor);
}
