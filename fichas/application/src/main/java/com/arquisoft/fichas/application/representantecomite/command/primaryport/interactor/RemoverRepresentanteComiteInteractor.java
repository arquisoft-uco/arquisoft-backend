package com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverRepresentanteComiteInteractor
        extends Interactor<RemoverRepresentanteComiteCommand, RemocionRepresentanteComiteResult> {
}
