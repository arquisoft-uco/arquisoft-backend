package com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.AgregarAsesorFichaFichasInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.mapper.AgregarAsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.AgregarAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.usecase.AgregarAsesorFichaFichasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAsesorFichaFichasInteractorImpl implements AgregarAsesorFichaFichasInteractor {

    private final AgregarAsesorFichaFichasUseCase agregarAsesorFichaFichasUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public AgregacionAsesorFichaResult ejecutar(AgregarAsesorFichaCommand command) {
        return agregarAsesorFichaFichasUseCase.ejecutar(AgregarAsesorFichaMapper.toDomain(command));
    }
}
