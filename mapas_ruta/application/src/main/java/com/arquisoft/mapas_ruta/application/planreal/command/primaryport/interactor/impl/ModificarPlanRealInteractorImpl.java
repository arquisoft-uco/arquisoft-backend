package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.ModificarPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper.ModificarPlanRealMapper;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.ModificarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.ModificarPlanRealUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarPlanRealInteractorImpl implements ModificarPlanRealInteractor {

    private final ModificarPlanRealUseCase modificarPlanRealUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(ModificarPlanRealCommand command) {
        var modificacion = ModificarPlanRealMapper.toDomain(command);

        modificarPlanRealUseCase.ejecutar(modificacion);
    }
}
