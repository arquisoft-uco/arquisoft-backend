package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.DescargarArchivoVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.DescargarArchivoVersionArtefactoMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.DescargarArchivoVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.DescargarArchivoVersionArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DescargarArchivoVersionArtefactoInteractorImpl implements DescargarArchivoVersionArtefactoInteractor {

    private final DescargarArchivoVersionArtefactoUseCase descargarArchivoVersionArtefactoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public EnlaceDescargaVersionArtefactoReadModel ejecutar(DescargarArchivoVersionArtefactoQuery entrada) {
        var criteria = DescargarArchivoVersionArtefactoMapper.toCriteria(entrada);
        return descargarArchivoVersionArtefactoUseCase.ejecutar(criteria);
    }
}
