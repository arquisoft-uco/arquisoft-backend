package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.ConsultarArtefactosEstudianteInteractor;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosEstudianteMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosEstudianteQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosEstudianteInteractorImpl implements ConsultarArtefactosEstudianteInteractor {

    private final ConsultarArtefactosEstudianteUseCase consultarArtefactosEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public PaginatedResult<ArtefactoEstudianteReadModel> ejecutar(ConsultarArtefactosEstudianteQuery entrada) {
        var criteria = ConsultarArtefactosEstudianteMapper.toCriteria(entrada);
        return consultarArtefactosEstudianteUseCase.ejecutar(criteria);
    }
}
