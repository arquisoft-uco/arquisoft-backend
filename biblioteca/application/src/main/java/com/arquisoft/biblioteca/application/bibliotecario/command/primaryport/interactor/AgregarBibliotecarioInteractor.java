package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.AgregarBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AgregarBibliotecarioInteractor
        extends Interactor<AgregarBibliotecarioCommand, AgregacionBibliotecarioResult> {
}
