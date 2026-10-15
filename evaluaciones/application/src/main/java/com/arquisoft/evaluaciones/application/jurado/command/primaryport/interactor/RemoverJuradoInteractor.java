package com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.evaluaciones.application.jurado.command.result.RemocionJuradoResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverJuradoInteractor
        extends Interactor<RemoverJuradoCommand, RemocionJuradoResult> {
}
