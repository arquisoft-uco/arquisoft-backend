package com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.CambiarEstadoDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.command.primaryport.mapper.CambiarEstadoDocumentacionMapper;
import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.CambiarEstadoDocumentacionCommand;
import com.arquisoft.biblioteca.application.documentacion.command.usecase.CambiarEstadoDocumentacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoDocumentacionInteractorImpl implements CambiarEstadoDocumentacionInteractor {

    private final CambiarEstadoDocumentacionUseCase cambiarEstadoDocumentacionUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public void ejecutar(CambiarEstadoDocumentacionCommand command) {
        cambiarEstadoDocumentacionUseCase.ejecutar(CambiarEstadoDocumentacionMapper.toDomain(command));
    }
}
