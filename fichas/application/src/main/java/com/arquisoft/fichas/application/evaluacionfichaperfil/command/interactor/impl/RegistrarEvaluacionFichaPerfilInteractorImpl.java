package com.arquisoft.fichas.application.evaluacionfichaperfil.command.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.interactor.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.mapper.RegistrarEvaluacionFichaPerfilMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
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
