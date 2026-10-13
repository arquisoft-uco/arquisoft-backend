package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RemoverBibliotecarioInteractor
        extends Interactor<RemoverBibliotecarioCommand, RemocionBibliotecarioResult> {
}
