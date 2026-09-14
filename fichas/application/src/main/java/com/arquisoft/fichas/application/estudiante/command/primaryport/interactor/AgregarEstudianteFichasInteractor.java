package com.arquisoft.fichas.application.estudiante.command.primaryport.interactor;

import com.arquisoft.fichas.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarEstudianteFichasInteractor
        extends Interactor<AgregarEstudianteCommand, AgregacionEstudianteResult> {
}
