package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.AgregarBibliotecarioInteractor;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.mapper.AgregarBibliotecarioMapper;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.AgregarBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.biblioteca.application.bibliotecario.command.usecase.AgregarBibliotecarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarBibliotecarioInteractorImpl implements AgregarBibliotecarioInteractor {

    private final AgregarBibliotecarioUseCase agregarBibliotecarioUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public AgregacionBibliotecarioResult ejecutar(AgregarBibliotecarioCommand command) {
        return agregarBibliotecarioUseCase.ejecutar(AgregarBibliotecarioMapper.toDomain(command));
    }
}
