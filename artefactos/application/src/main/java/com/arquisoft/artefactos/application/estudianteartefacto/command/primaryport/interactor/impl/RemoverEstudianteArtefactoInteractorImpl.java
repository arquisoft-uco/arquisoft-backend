package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.RemoverEstudianteArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper.RemoverEstudianteArtefactoMapper;
import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.RemoverEstudianteArtefactoCommand;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.RemoverEstudianteArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteArtefactoInteractorImpl implements RemoverEstudianteArtefactoInteractor {

    private final RemoverEstudianteArtefactoUseCase removerEstudianteArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(RemoverEstudianteArtefactoCommand command) {
        removerEstudianteArtefactoUseCase.ejecutar(RemoverEstudianteArtefactoMapper.toDomain(command));
    }
}
