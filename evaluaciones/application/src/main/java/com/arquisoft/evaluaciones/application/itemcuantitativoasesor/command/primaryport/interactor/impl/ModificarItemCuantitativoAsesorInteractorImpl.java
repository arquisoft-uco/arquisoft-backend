package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.ModificarItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper.ModificarItemCuantitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.ModificarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.ModificarItemCuantitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarItemCuantitativoAsesorInteractorImpl
        implements ModificarItemCuantitativoAsesorInteractor {

    private final ModificarItemCuantitativoAsesorUseCase modificarItemCuantitativoAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(ModificarItemCuantitativoAsesorCommand command) {
        modificarItemCuantitativoAsesorUseCase.ejecutar(
                ModificarItemCuantitativoAsesorMapper.toDomain(command));
    }
}
