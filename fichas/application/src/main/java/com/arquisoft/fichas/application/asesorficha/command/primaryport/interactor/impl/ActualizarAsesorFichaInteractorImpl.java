package com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.ActualizarAsesorFichaInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.mapper.ActualizarAsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.ActualizarAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.ActualizacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.usecase.ActualizarAsesorFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarAsesorFichaInteractorImpl implements ActualizarAsesorFichaInteractor {

    private final ActualizarAsesorFichaUseCase actualizarAsesorFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public ActualizacionAsesorFichaResult ejecutar(ActualizarAsesorFichaCommand command) {
        return actualizarAsesorFichaUseCase.ejecutar(ActualizarAsesorFichaMapper.toDomain(command));
    }
}
