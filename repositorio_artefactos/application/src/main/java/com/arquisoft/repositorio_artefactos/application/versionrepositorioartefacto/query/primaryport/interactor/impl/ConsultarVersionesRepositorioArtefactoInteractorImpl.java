package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.ConsultarVersionesRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper.ConsultarVersionesRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.ConsultarVersionesRepositorioArtefactoQuery;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.ConsultarVersionesRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesRepositorioArtefactoInteractorImpl
        implements ConsultarVersionesRepositorioArtefactoInteractor {

    private final ConsultarVersionesRepositorioArtefactoUseCase consultarVersionesRepositorioArtefactoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "repositorioArtefactosTransactionManager")
    public List<VersionRepositorioArtefactoReadModel> ejecutar(ConsultarVersionesRepositorioArtefactoQuery entrada) {
        var criteria = ConsultarVersionesRepositorioArtefactoMapper.toCriteria(entrada);
        return consultarVersionesRepositorioArtefactoUseCase.ejecutar(criteria);
    }
}
