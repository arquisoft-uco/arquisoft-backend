package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.RemoverPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper.RemoverPlanRealMapper;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.RemoverPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.RemoverPlanRealUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverPlanRealInteractorImpl implements RemoverPlanRealInteractor {

    private final RemoverPlanRealUseCase removerPlanRealUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(RemoverPlanRealCommand command) {
        var remocion = RemoverPlanRealMapper.toDomain(command);

        removerPlanRealUseCase.ejecutar(remocion);
    }
}
