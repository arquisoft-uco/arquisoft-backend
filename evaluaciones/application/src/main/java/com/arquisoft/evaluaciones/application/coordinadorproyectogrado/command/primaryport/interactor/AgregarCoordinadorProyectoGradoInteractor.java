package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.interactor;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model.AgregarCoordinadorProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarCoordinadorProyectoGradoInteractor
        extends Interactor<AgregarCoordinadorProyectoGradoCommand, AgregacionCoordinadorProyectoGradoResult> {
}
