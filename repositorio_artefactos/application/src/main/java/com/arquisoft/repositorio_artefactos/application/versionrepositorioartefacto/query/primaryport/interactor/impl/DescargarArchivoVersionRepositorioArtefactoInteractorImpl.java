package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.DescargarArchivoVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper.DescargarArchivoVersionRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.DescargarArchivoVersionRepositorioArtefactoQuery;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.DescargarArchivoVersionRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DescargarArchivoVersionRepositorioArtefactoInteractorImpl
        implements DescargarArchivoVersionRepositorioArtefactoInteractor {

    private final DescargarArchivoVersionRepositorioArtefactoUseCase descargarArchivoVersionRepositorioArtefactoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "repositorioArtefactosTransactionManager")
    public EnlaceDescargaVersionReadModel ejecutar(DescargarArchivoVersionRepositorioArtefactoQuery entrada) {
        var criteria = DescargarArchivoVersionRepositorioArtefactoMapper.toCriteria(entrada);
        return descargarArchivoVersionRepositorioArtefactoUseCase.ejecutar(criteria);
    }
}
