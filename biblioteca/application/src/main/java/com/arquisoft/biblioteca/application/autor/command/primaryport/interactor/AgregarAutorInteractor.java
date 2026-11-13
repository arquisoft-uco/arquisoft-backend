package com.arquisoft.biblioteca.application.autor.command.primaryport.interactor;

import com.arquisoft.biblioteca.application.autor.command.primaryport.model.AgregarAutorCommand;
import com.arquisoft.biblioteca.application.autor.command.result.AgregacionAutorResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarAutorInteractor
        extends Interactor<AgregarAutorCommand, AgregacionAutorResult> {
}
