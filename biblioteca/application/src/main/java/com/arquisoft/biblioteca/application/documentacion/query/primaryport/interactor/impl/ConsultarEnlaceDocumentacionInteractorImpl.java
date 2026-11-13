package com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.ConsultarEnlaceDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper.ConsultarEnlaceDocumentacionMapper;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarEnlaceDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.ConsultarEnlaceDocumentacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEnlaceDocumentacionInteractorImpl implements ConsultarEnlaceDocumentacionInteractor {

    private final ConsultarEnlaceDocumentacionUseCase consultarEnlaceDocumentacionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "bibliotecaTransactionManager")
    public EnlaceDocumentacionReadModel ejecutar(ConsultarEnlaceDocumentacionQuery entrada) {
        var criteria = ConsultarEnlaceDocumentacionMapper.toCriteria(entrada);
        return consultarEnlaceDocumentacionUseCase.ejecutar(criteria);
    }
}
