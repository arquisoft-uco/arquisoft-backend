package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.AsignarJuradosEvaluacionCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;
import java.util.UUID;

public interface AsignarJuradosEvaluacionInteractor
        extends Interactor<AsignarJuradosEvaluacionCommand, List<UUID>> {
}
