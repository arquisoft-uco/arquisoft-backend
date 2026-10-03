package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.RemoverBibliotecarioInteractor;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.mapper.RemoverBibliotecarioMapper;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
import com.arquisoft.biblioteca.application.bibliotecario.command.usecase.RemoverBibliotecarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverBibliotecarioInteractorImpl implements RemoverBibliotecarioInteractor {

    private final RemoverBibliotecarioUseCase removerBibliotecarioUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public RemocionBibliotecarioResult ejecutar(RemoverBibliotecarioCommand command) {
        return removerBibliotecarioUseCase.ejecutar(RemoverBibliotecarioMapper.toDomain(command));
    }
}
