package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.interactor;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarEstudiantesProyectoGradoInteractor
        extends Interactor<AgregarEstudiantesProyectoGradoCommand, AgregacionEstudiantesProyectoGradoResult> {
}
