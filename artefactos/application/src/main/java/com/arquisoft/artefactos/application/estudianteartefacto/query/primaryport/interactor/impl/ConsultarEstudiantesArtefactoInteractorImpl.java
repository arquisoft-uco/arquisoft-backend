package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.ConsultarEstudiantesArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper.ConsultarEstudiantesArtefactoMapper;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarEstudiantesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.ConsultarEstudiantesArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesArtefactoInteractorImpl implements ConsultarEstudiantesArtefactoInteractor {

    private final ConsultarEstudiantesArtefactoUseCase consultarEstudiantesArtefactoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public List<EstudianteArtefactoReadModel> ejecutar(ConsultarEstudiantesArtefactoQuery entrada) {
        var criteria = ConsultarEstudiantesArtefactoMapper.toCriteria(entrada);
        return consultarEstudiantesArtefactoUseCase.ejecutar(criteria);
    }
}
