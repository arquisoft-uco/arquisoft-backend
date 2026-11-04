package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.CambiarNombreRepositorioVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.mapper.CambiarNombreRepositorioVersionRepositorioArtefactoMapper;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.CambiarNombreRepositorioVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.CambiarNombreRepositorioVersionRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarNombreRepositorioVersionRepositorioArtefactoInteractorImpl
        implements CambiarNombreRepositorioVersionRepositorioArtefactoInteractor {

    private final CambiarNombreRepositorioVersionRepositorioArtefactoUseCase cambiarNombreRepositorioUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(CambiarNombreRepositorioVersionRepositorioArtefactoCommand command) {
        cambiarNombreRepositorioUseCase.ejecutar(
                CambiarNombreRepositorioVersionRepositorioArtefactoMapper.toDomain(command));
    }
}
