package com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.DescargarDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper.DescargarDocumentacionMapper;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.DescargarDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.DescargarDocumentacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DescargarDocumentacionInteractorImpl implements DescargarDocumentacionInteractor {

    private final DescargarDocumentacionUseCase descargarDocumentacionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "bibliotecaTransactionManager")
    public EnlaceDescargaDocumentacionReadModel ejecutar(DescargarDocumentacionQuery entrada) {
        var criteria = DescargarDocumentacionMapper.toCriteria(entrada);
        return descargarDocumentacionUseCase.ejecutar(criteria);
    }
}
