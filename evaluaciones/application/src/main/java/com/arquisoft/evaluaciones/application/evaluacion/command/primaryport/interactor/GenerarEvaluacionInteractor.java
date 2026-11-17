package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.shared.interactor.Interactor;

public interface GenerarEvaluacionInteractor
        extends Interactor<GenerarEvaluacionCommand, GeneracionEvaluacionResult> {
}
