package com.arquisoft.fichas.application.coordinador.command.primaryport.interactor;

import com.arquisoft.fichas.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.fichas.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarCoordinadorInteractor
        extends Interactor<AgregarCoordinadorCommand, AgregacionCoordinadorResult> {
}
