package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.GenerarPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper.GenerarPlanRealMapper;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.GenerarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.result.GeneracionPlanRealResult;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.GenerarPlanRealUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GenerarPlanRealInteractorImpl implements GenerarPlanRealInteractor {

    private final GenerarPlanRealUseCase generarPlanRealUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public GeneracionPlanRealResult ejecutar(GenerarPlanRealCommand command) {
        return generarPlanRealUseCase.ejecutar(GenerarPlanRealMapper.toDomain(command));
    }
}
