package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.ModificarPlanEstimadoInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper.ModificarPlanEstimadoMapper;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.ModificarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.ModificarPlanEstimadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarPlanEstimadoInteractorImpl implements ModificarPlanEstimadoInteractor {

    private final ModificarPlanEstimadoUseCase modificarPlanEstimadoUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(ModificarPlanEstimadoCommand command) {
        var modificacion = ModificarPlanEstimadoMapper.toDomain(command);

        modificarPlanEstimadoUseCase.ejecutar(modificacion);
    }
}
