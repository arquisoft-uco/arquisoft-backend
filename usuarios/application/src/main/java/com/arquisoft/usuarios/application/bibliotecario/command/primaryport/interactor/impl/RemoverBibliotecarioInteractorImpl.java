package com.arquisoft.usuarios.application.bibliotecario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.interactor.RemoverBibliotecarioInteractor;
import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.mapper.RemoverBibliotecarioMapper;
import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.usuarios.application.bibliotecario.command.usecase.RemoverBibliotecarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverBibliotecarioInteractorImpl implements RemoverBibliotecarioInteractor {

    private final RemoverBibliotecarioUseCase removerBibliotecarioUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(RemoverBibliotecarioCommand command) {
        removerBibliotecarioUseCase.ejecutar(RemoverBibliotecarioMapper.toDomain(command));
    }
}
