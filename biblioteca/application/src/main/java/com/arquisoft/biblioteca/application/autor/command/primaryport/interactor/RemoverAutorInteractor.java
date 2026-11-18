package com.arquisoft.biblioteca.application.autor.command.primaryport.interactor;

import com.arquisoft.biblioteca.application.autor.command.primaryport.model.RemoverAutorCommand;
import com.arquisoft.biblioteca.application.autor.command.result.RemocionAutorResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverAutorInteractor
        extends Interactor<RemoverAutorCommand, RemocionAutorResult> {
}
