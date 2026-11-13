package com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.interactor.ConsultarAutoresDocumentacionInteractor;
import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.mapper.ConsultarAutoresDocumentacionMapper;
import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model.ConsultarAutoresDocumentacionQuery;
import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.biblioteca.application.autordocumentacion.query.usecase.ConsultarAutoresDocumentacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAutoresDocumentacionInteractorImpl implements ConsultarAutoresDocumentacionInteractor {

    private final ConsultarAutoresDocumentacionUseCase consultarAutoresDocumentacionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "bibliotecaTransactionManager")
    public List<AutorDocumentacionReadModel> ejecutar(ConsultarAutoresDocumentacionQuery entrada) {
        var criteria = ConsultarAutoresDocumentacionMapper.toCriteria(entrada);
        return consultarAutoresDocumentacionUseCase.ejecutar(criteria);
    }
}
