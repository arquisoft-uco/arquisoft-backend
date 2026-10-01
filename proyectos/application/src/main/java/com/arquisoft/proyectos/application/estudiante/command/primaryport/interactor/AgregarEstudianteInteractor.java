package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarEstudianteInteractor
        extends Interactor<AgregarEstudianteCommand, AgregacionEstudianteResult> {
}
