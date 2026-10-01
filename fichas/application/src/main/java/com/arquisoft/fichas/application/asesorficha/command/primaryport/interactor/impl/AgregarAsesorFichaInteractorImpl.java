package com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.AgregarAsesorFichaInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.mapper.AgregarAsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.AgregarAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.usecase.AgregarAsesorFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAsesorFichaInteractorImpl implements AgregarAsesorFichaInteractor {

    private final AgregarAsesorFichaUseCase agregarAsesorFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public AgregacionAsesorFichaResult ejecutar(AgregarAsesorFichaCommand command) {
        return agregarAsesorFichaUseCase.ejecutar(AgregarAsesorFichaMapper.toDomain(command));
    }
}
