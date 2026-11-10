package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.AportarPlanEstimadoInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper.AportarPlanEstimadoMapper;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AportarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.AportarPlanEstimadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AportarPlanEstimadoInteractorImpl implements AportarPlanEstimadoInteractor {

    private final AportarPlanEstimadoUseCase aportarPlanEstimadoUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(AportarPlanEstimadoCommand command) {
        var aporte = AportarPlanEstimadoMapper.toDomain(command);

        aportarPlanEstimadoUseCase.ejecutar(aporte);
    }
}
