package com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.interactor.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.mapper.RegistrarEvaluacionFichaPerfilMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.RegistrarEvaluacionFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionFichaPerfilInteractorImpl implements RegistrarEvaluacionFichaPerfilInteractor {

    private final RegistrarEvaluacionFichaPerfilUseCase registrarEvaluacionFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarEvaluacionFichaPerfilCommand command) {
        return registrarEvaluacionFichaPerfilUseCase.ejecutar(
                RegistrarEvaluacionFichaPerfilMapper.toDomain(command));
    }
}
