package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.RemoverPlanEstimadoInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper.RemoverPlanEstimadoMapper;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.RemoverPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.RemoverPlanEstimadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverPlanEstimadoInteractorImpl implements RemoverPlanEstimadoInteractor {

    private final RemoverPlanEstimadoUseCase removerPlanEstimadoUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(RemoverPlanEstimadoCommand command) {
        var remocion = RemoverPlanEstimadoMapper.toDomain(command);

        removerPlanEstimadoUseCase.ejecutar(remocion);
    }
}
