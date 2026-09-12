package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.ModificarItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper.ModificarItemCuantitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.ModificarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.ModificarItemCuantitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarItemCuantitativoJuradoInteractorImpl
        implements ModificarItemCuantitativoJuradoInteractor {

    private final ModificarItemCuantitativoJuradoUseCase modificarItemCuantitativoJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(ModificarItemCuantitativoJuradoCommand command) {
        modificarItemCuantitativoJuradoUseCase.ejecutar(
                ModificarItemCuantitativoJuradoMapper.toDomain(command));
    }
}
