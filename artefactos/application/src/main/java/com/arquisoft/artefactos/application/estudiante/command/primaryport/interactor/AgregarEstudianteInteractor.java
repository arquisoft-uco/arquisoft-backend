package com.arquisoft.artefactos.application.estudiante.command.primaryport.interactor;

import com.arquisoft.artefactos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.artefactos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarEstudianteInteractor
        extends Interactor<AgregarEstudianteCommand, AgregacionEstudianteResult> {
}
