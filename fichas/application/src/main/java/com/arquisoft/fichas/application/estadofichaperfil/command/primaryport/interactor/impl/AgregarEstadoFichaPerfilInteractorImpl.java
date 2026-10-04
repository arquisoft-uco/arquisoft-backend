package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.AgregarEstadoFichaPerfilInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper.AgregarEstadoFichaPerfilMapper;
import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoFichaPerfilCommand;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AgregarEstadoFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoFichaPerfilInteractorImpl implements AgregarEstadoFichaPerfilInteractor {

    private final AgregarEstadoFichaPerfilUseCase agregarEstadoFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarEstadoFichaPerfilCommand command) {
        return agregarEstadoFichaPerfilUseCase.ejecutar(AgregarEstadoFichaPerfilMapper.toDomain(command));
    }
}
