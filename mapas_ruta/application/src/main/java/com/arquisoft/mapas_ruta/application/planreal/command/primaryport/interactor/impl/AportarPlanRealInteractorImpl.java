package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.AportarPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper.AportarPlanRealMapper;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.AportarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.AportarPlanRealUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AportarPlanRealInteractorImpl implements AportarPlanRealInteractor {

    private final AportarPlanRealUseCase aportarPlanRealUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(AportarPlanRealCommand command) {
        var aporte = AportarPlanRealMapper.toDomain(command);

        aportarPlanRealUseCase.ejecutar(aporte);
    }
}
