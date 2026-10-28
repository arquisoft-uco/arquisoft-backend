package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.AgregarPlanEstimadoInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper.AgregarPlanEstimadoMapper;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AgregarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.AgregarPlanEstimadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarPlanEstimadoInteractorImpl implements AgregarPlanEstimadoInteractor {

    private final AgregarPlanEstimadoUseCase agregarPlanEstimadoUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public UUID ejecutar(AgregarPlanEstimadoCommand command) {
        var planEstimado = AgregarPlanEstimadoMapper.toDomain(command);

        return agregarPlanEstimadoUseCase.ejecutar(planEstimado);
    }
}
