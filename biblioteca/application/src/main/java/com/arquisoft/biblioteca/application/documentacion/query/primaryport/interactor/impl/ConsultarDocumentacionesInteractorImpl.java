package com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.ConsultarDocumentacionesInteractor;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper.ConsultarDocumentacionesMapper;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarDocumentacionesQuery;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.ConsultarDocumentacionesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarDocumentacionesInteractorImpl implements ConsultarDocumentacionesInteractor {

    private final ConsultarDocumentacionesUseCase consultarDocumentacionesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "bibliotecaTransactionManager")
    public PaginatedResult<DocumentacionReadModel> ejecutar(ConsultarDocumentacionesQuery entrada) {
        var criteria = ConsultarDocumentacionesMapper.toCriteria(entrada);
        return consultarDocumentacionesUseCase.ejecutar(criteria);
    }
}
