package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.AsignarEstudiantesArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper.AsignarEstudiantesArtefactoMapper;
import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesArtefactoInteractorImpl implements AsignarEstudiantesArtefactoInteractor {

    private final AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(AsignarEstudiantesArtefactoCommand command) {
        asignarEstudiantesArtefactoUseCase.ejecutar(AsignarEstudiantesArtefactoMapper.toDomain(command));
    }
}
