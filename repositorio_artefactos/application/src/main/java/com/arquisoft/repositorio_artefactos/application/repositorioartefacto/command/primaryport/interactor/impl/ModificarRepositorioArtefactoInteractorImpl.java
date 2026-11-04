package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.ModificarRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.mapper.ModificarRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.ModificarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.ModificarRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarRepositorioArtefactoInteractorImpl implements ModificarRepositorioArtefactoInteractor {

    private final ModificarRepositorioArtefactoUseCase modificarRepositorioArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "repositorioArtefactosTransactionManager")
    public void ejecutar(ModificarRepositorioArtefactoCommand command) {
        modificarRepositorioArtefactoUseCase.ejecutar(ModificarRepositorioArtefactoMapper.toDomain(command));
    }
}
