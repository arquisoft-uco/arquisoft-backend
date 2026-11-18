package com.arquisoft.biblioteca.application.estadodocumentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.primaryport.interactor.ConsultarEstadosDocumentacionInteractor;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.usecase.ConsultarEstadosDocumentacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosDocumentacionInteractorImpl implements ConsultarEstadosDocumentacionInteractor {

    private final ConsultarEstadosDocumentacionUseCase consultarEstadosDocumentacionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "bibliotecaTransactionManager")
    public List<EstadoDocumentacionReadModel> ejecutar() {
        return consultarEstadosDocumentacionUseCase.ejecutar();
    }
}
