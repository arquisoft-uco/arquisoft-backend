package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.AgregarEstadoAprobacionFichaPerfilInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper.AgregarEstadoAprobacionFichaPerfilMapper;
import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoAprobacionFichaPerfilCommand;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AgregarEstadoAprobacionFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoAprobacionFichaPerfilInteractorImpl implements AgregarEstadoAprobacionFichaPerfilInteractor {

    private final AgregarEstadoAprobacionFichaPerfilUseCase agregarEstadoAprobacionFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarEstadoAprobacionFichaPerfilCommand command) {
        return agregarEstadoAprobacionFichaPerfilUseCase.ejecutar(
                AgregarEstadoAprobacionFichaPerfilMapper.toDomain(command));
    }
}
