package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.shared.interactor.Interactor;

public interface ActualizarEstudianteInteractor
        extends Interactor<ActualizarEstudianteCommand, ActualizacionEstudianteResult> {
}
