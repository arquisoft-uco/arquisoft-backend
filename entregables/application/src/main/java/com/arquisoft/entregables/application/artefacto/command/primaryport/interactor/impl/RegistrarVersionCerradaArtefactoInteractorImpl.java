package com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.RegistrarVersionCerradaArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.mapper.RegistrarVersionCerradaArtefactoMapper;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.RegistroVersionCerradaArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.usecase.RegistrarVersionCerradaArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarVersionCerradaArtefactoInteractorImpl implements RegistrarVersionCerradaArtefactoInteractor {

    private final RegistrarVersionCerradaArtefactoUseCase registrarVersionCerradaArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public RegistroVersionCerradaArtefactoResult ejecutar(RegistrarVersionCerradaArtefactoCommand command) {
        return registrarVersionCerradaArtefactoUseCase.ejecutar(
                RegistrarVersionCerradaArtefactoMapper.toDomain(command));
    }
}
