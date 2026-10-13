package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.RemoverAdministradorInteractor;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.mapper.RemoverAdministradorMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.RemoverAdministradorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverAdministradorInteractorImpl implements RemoverAdministradorInteractor {

    private final RemoverAdministradorUseCase removerAdministradorUseCase;

    @Override
    @Transactional(transactionManager = "repositorioArtefactosTransactionManager")
    public RemocionAdministradorResult ejecutar(RemoverAdministradorCommand command) {
        return removerAdministradorUseCase.ejecutar(RemoverAdministradorMapper.toDomain(command));
    }
}
