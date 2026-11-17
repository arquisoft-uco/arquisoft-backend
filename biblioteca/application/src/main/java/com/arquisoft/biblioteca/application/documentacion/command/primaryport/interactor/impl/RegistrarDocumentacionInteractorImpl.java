package com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.RegistrarDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.command.primaryport.mapper.RegistrarDocumentacionMapper;
import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.RegistrarDocumentacionCommand;
import com.arquisoft.biblioteca.application.documentacion.command.usecase.RegistrarDocumentacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarDocumentacionInteractorImpl implements RegistrarDocumentacionInteractor {

    private final RegistrarDocumentacionUseCase registrarDocumentacionUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public UUID ejecutar(RegistrarDocumentacionCommand command) {
        return registrarDocumentacionUseCase.ejecutar(RegistrarDocumentacionMapper.toDomain(command));
    }
}
