package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.AgregarAdministradorInteractor;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.mapper.AgregarAdministradorMapper;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.AgregarAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.AgregacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.AgregarAdministradorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAdministradorInteractorImpl implements AgregarAdministradorInteractor {

    private final AgregarAdministradorUseCase agregarAdministradorUseCase;

    @Override
    @Transactional(transactionManager = "repositorioArtefactosTransactionManager")
    public AgregacionAdministradorResult ejecutar(AgregarAdministradorCommand command) {
        return agregarAdministradorUseCase.ejecutar(AgregarAdministradorMapper.toDomain(command));
    }
}
