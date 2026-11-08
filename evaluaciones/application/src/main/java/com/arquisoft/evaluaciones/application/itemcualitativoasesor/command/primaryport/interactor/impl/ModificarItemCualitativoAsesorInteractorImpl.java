package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.ModificarItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper.ModificarItemCualitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.ModificarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.ModificarItemCualitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarItemCualitativoAsesorInteractorImpl
        implements ModificarItemCualitativoAsesorInteractor {

    private final ModificarItemCualitativoAsesorUseCase modificarItemCualitativoAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(ModificarItemCualitativoAsesorCommand command) {
        modificarItemCualitativoAsesorUseCase.ejecutar(
                ModificarItemCualitativoAsesorMapper.toDomain(command));
    }
}
