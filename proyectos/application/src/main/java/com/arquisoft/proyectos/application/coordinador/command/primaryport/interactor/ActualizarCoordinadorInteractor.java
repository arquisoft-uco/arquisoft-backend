package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.ActualizarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.shared.interactor.Interactor;

public interface ActualizarCoordinadorInteractor
        extends Interactor<ActualizarCoordinadorCommand, ActualizacionCoordinadorResult> {
}
