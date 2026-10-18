package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.AgregarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.AgregacionAdministradorResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarAdministradorInteractor
        extends Interactor<AgregarAdministradorCommand, AgregacionAdministradorResult> {
}
