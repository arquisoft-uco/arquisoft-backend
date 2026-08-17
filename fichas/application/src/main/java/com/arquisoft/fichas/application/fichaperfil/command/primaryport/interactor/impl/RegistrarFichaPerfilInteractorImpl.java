package com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper.RegistrarFichaPerfilMapper;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilInteractorImpl implements RegistrarFichaPerfilInteractor {

    private final RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        return registrarFichaPerfilUseCase.ejecutar(RegistrarFichaPerfilMapper.toDomain(command));
    }
}
