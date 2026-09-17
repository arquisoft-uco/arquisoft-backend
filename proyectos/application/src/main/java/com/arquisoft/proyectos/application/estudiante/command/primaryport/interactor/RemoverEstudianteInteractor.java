package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverEstudianteInteractor
        extends Interactor<RemoverEstudianteCommand, RemocionEstudianteResult> {
}
