package com.arquisoft.fichas.application.estudiante.command.primaryport.interactor;

import com.arquisoft.fichas.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverEstudianteInteractor
        extends Interactor<RemoverEstudianteCommand, RemocionEstudianteResult> {
}
