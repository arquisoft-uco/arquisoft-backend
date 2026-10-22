package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.interactor;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverEstudianteProyectoGradoInteractor
        extends Interactor<RemoverEstudianteProyectoGradoCommand, RemocionEstudianteProyectoGradoResult> {
}
