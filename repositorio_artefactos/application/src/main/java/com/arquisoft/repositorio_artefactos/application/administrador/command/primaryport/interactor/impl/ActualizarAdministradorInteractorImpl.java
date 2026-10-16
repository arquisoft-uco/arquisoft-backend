package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.ActualizarAdministradorInteractor;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.mapper.ActualizarAdministradorMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.ActualizarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.ActualizacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.ActualizarAdministradorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarAdministradorInteractorImpl implements ActualizarAdministradorInteractor {

    private final ActualizarAdministradorUseCase actualizarAdministradorUseCase;

    @Override
    @Transactional(transactionManager = "repositorioArtefactosTransactionManager")
    public ActualizacionAdministradorResult ejecutar(ActualizarAdministradorCommand command) {
        return actualizarAdministradorUseCase.ejecutar(ActualizarAdministradorMapper.toDomain(command));
    }
}
