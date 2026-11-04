package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.ConsultarParticipacionesArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper.ConsultarParticipacionesArtefactoMapper;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.ConsultarParticipacionesArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarParticipacionesArtefactoInteractorImpl implements ConsultarParticipacionesArtefactoInteractor {

    private final ConsultarParticipacionesArtefactoUseCase consultarParticipacionesArtefactoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public List<ParticipacionArtefactoReadModel> ejecutar(ConsultarParticipacionesArtefactoQuery entrada) {
        var criteria = ConsultarParticipacionesArtefactoMapper.toCriteria(entrada);
        return consultarParticipacionesArtefactoUseCase.ejecutar(criteria);
    }
}
