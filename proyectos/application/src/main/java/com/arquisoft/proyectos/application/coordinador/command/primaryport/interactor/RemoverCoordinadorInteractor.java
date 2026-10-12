package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverCoordinadorInteractor
        extends Interactor<RemoverCoordinadorCommand, RemocionCoordinadorResult> {
}
