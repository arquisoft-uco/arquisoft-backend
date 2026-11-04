package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverAdministradorInteractor
        extends Interactor<RemoverAdministradorCommand, RemocionAdministradorResult> {
}
